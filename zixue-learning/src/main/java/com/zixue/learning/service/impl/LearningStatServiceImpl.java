package com.zixue.learning.service.impl;

import com.zixue.common.dto.DurationSummaryVO;
import com.zixue.learning.entity.LearningStat;
import com.zixue.learning.feign.ProgressFeignClient;
import com.zixue.learning.mapper.LearningStatMapper;
import com.zixue.learning.service.LearningStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 学情聚合：调用进度服务获取汇总，计算观看完成率 / 平均时长 / 跳出率并落库。
 */
@Service
public class LearningStatServiceImpl implements LearningStatService {

    private static final Logger log = LoggerFactory.getLogger(LearningStatServiceImpl.class);

    private final ProgressFeignClient progressFeignClient;
    private final LearningStatMapper statMapper;

    public LearningStatServiceImpl(ProgressFeignClient progressFeignClient, LearningStatMapper statMapper) {
        this.progressFeignClient = progressFeignClient;
        this.statMapper = statMapper;
    }

    @Override
    public void aggregate(Long videoId) {
        DurationSummaryVO summary = progressFeignClient.getDurationSummary(videoId).getData();
        if (summary == null || summary.getWatchUv() == null || summary.getWatchUv() == 0) {
            log.info("视频 {} 暂无观看记录，跳过聚合", videoId);
            return;
        }

        int watchUv = summary.getWatchUv();
        double finishRate = round2(summary.getFinishCount() * 100.0 / watchUv);
        double bounceRate = round2(summary.getBounceCount() * 100.0 / watchUv);
        int avgValidDuration = (int) (summary.getTotalValidDuration() / watchUv);

        LearningStat stat = new LearningStat();
        stat.setStatDim("video");
        stat.setDimId(videoId);
        stat.setStatDate(LocalDate.now());
        stat.setWatchUv(watchUv);
        stat.setFinishRate(finishRate);
        stat.setBounceRate(bounceRate);
        stat.setAvgValidDuration(avgValidDuration);
        statMapper.upsert(stat);
        log.info("视频 {} 学情聚合完成: uv={}, 完成率={}%, 跳出率={}%, 平均时长={}s",
                videoId, watchUv, finishRate, bounceRate, avgValidDuration);
    }

    @Override
    public LearningStat getStat(Long videoId) {
        return statMapper.selectLatest("video", videoId);
    }

    private double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
