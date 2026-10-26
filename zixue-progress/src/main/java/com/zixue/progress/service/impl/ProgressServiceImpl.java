package com.zixue.progress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zixue.common.dto.DurationSummaryVO;
import com.zixue.common.enums.ErrorCode;
import com.zixue.common.exception.BizException;
import com.zixue.progress.anticheat.AntiCheatResult;
import com.zixue.progress.anticheat.AntiCheatValidator;
import com.zixue.progress.anticheat.DurationCalculator;
import com.zixue.progress.dto.ProgressReportDTO;
import com.zixue.progress.dto.ProgressVO;
import com.zixue.progress.entity.LearnProgress;
import com.zixue.progress.mapper.LearnDurationMapper;
import com.zixue.progress.mapper.LearnProgressMapper;
import com.zixue.progress.mq.ProgressMessage;
import com.zixue.progress.mq.ProgressProducer;
import com.zixue.progress.redis.ProgressRedisService;
import com.zixue.progress.service.ProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 进度上报编排：防刷 → Lua 写 Redis（快路径）→ 发 MQ 异步落库（慢路径）。
 */
@Service
public class ProgressServiceImpl implements ProgressService {

    private static final Logger log = LoggerFactory.getLogger(ProgressServiceImpl.class);

    private final ProgressRedisService redisService;
    private final LearnProgressMapper progressMapper;
    private final LearnDurationMapper durationMapper;
    private final AntiCheatValidator antiCheatValidator;
    private final DurationCalculator durationCalculator;
    private final ProgressProducer producer;

    public ProgressServiceImpl(ProgressRedisService redisService,
                               LearnProgressMapper progressMapper,
                               LearnDurationMapper durationMapper,
                               AntiCheatValidator antiCheatValidator,
                               DurationCalculator durationCalculator,
                               ProgressProducer producer) {
        this.redisService = redisService;
        this.progressMapper = progressMapper;
        this.durationMapper = durationMapper;
        this.antiCheatValidator = antiCheatValidator;
        this.durationCalculator = durationCalculator;
        this.producer = producer;
    }

    @Override
    public void report(ProgressReportDTO dto) {
        long currentTime = System.currentTimeMillis();

        // 读取上次进度（Redis 优先，未命中回源 DB）
        LearnProgress previous = redisService.getProgress(dto.getUserId(), dto.getVideoId());
        if (previous == null) {
            previous = progressMapper.selectOne(new LambdaQueryWrapper<LearnProgress>()
                    .eq(LearnProgress::getUserId, dto.getUserId())
                    .eq(LearnProgress::getVideoId, dto.getVideoId()));
        }

        // 三重防刷校验
        AntiCheatResult antiCheat = antiCheatValidator.validate(dto, previous, currentTime);
        if (!antiCheat.isAccepted()) {
            throw new BizException(ErrorCode.ANTI_CHEAT_REJECTED);
        }

        // 有效时长计算（拖动/断档/切设备不计）
        long validSeconds = 0;
        if (antiCheat.isCountDuration() && previous != null) {
            validSeconds = durationCalculator.calcValidDurationSeconds(
                    dto.getSpeed(),
                    antiCheat.getIntervalSeconds(),
                    dto.getPosition() - previous.getPosition());
        }

        // 快路径：Lua 原子写 Redis（时间戳裁决，多端进度以 Redis 为最终基准）
        redisService.updateProgress(dto, currentTime);

        // 慢路径：发 MQ，异步批量落库
        ProgressMessage message = new ProgressMessage(
                dto.getUserId(), dto.getVideoId(), dto.getPosition(), dto.getDuration(),
                dto.getSpeed(), currentTime, dto.getDeviceId(), validSeconds);
        producer.send(message);
    }

    @Override
    public ProgressVO getProgress(Long userId, Long videoId) {
        LearnProgress progress = redisService.getProgress(userId, videoId);
        if (progress == null) {
            progress = progressMapper.selectOne(new LambdaQueryWrapper<LearnProgress>()
                    .eq(LearnProgress::getUserId, userId)
                    .eq(LearnProgress::getVideoId, videoId));
            if (progress != null) {
                redisService.saveProgress(progress);
            }
        }
        if (progress == null) {
            throw new BizException(ErrorCode.PROGRESS_NOT_FOUND);
        }
        return toVO(progress);
    }

    @Override
    public DurationSummaryVO getDurationSummary(Long videoId) {
        DurationSummaryVO summary = progressMapper.selectProgressStats(videoId);
        if (summary == null) {
            summary = new DurationSummaryVO();
        }
        summary.setVideoId(videoId);
        summary.setTotalValidDuration(durationMapper.selectTotalValidDuration(videoId));
        return summary;
    }

    @Override
    public List<Long> listActiveVideoIds() {
        return progressMapper.selectActiveVideoIds();
    }

    private ProgressVO toVO(LearnProgress p) {
        ProgressVO vo = new ProgressVO();
        vo.setUserId(p.getUserId());
        vo.setVideoId(p.getVideoId());
        vo.setPosition(p.getPosition());
        vo.setDuration(p.getDuration());
        vo.setSpeed(p.getSpeed());
        vo.setLastReportTime(p.getLastReportTime());
        vo.setDeviceId(p.getDeviceId());
        return vo;
    }
}
