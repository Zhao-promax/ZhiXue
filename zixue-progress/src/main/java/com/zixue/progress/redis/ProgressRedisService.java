package com.zixue.progress.redis;

import com.zixue.common.constant.CommonConstants;
import com.zixue.progress.dto.ProgressReportDTO;
import com.zixue.progress.entity.LearnProgress;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习进度 Redis 缓存服务（Hash + Lua 原子更新）。
 */
@Service
public class ProgressRedisService {

    private static final String KEY_PREFIX = "learn:progress:";

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<Long> updateScript =
            new DefaultRedisScript<>(LuaScripts.UPDATE_PROGRESS, Long.class);

    public ProgressRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 读取实时进度，不存在返回 null。
     */
    public LearnProgress getProgress(Long userId, Long videoId) {
        Map<Object, Object> hash = redisTemplate.opsForHash().entries(key(userId, videoId));
        if (hash == null || hash.isEmpty()) {
            return null;
        }
        LearnProgress progress = new LearnProgress();
        progress.setUserId(userId);
        progress.setVideoId(videoId);
        progress.setPosition(toInt(hash.get("position")));
        progress.setDuration(toInt(hash.get("duration")));
        progress.setSpeed(toDouble(hash.get("speed")));
        progress.setLastReportTime(toLong(hash.get("last_report_time")));
        progress.setDeviceId(toStr(hash.get("device_id")));
        return progress;
    }

    /**
     * Lua 原子更新进度，按上报时间戳裁决冲突。返回 true 表示已更新，false 表示被更新的数据覆盖（旧数据被拒绝）。
     */
    public boolean updateProgress(ProgressReportDTO dto, long reportTime) {
        String key = key(dto.getUserId(), dto.getVideoId());
        Long result = redisTemplate.execute(updateScript,
                List.of(key),
                String.valueOf(dto.getPosition()),
                String.valueOf(dto.getDuration()),
                String.valueOf(dto.getSpeed()),
                String.valueOf(reportTime),
                dto.getDeviceId(),
                String.valueOf(CommonConstants.PROGRESS_TTL_SECONDS));
        return result != null && result == 1L;
    }

    /**
     * 回填缓存（DB 回源后写入 Redis）。
     */
    public void saveProgress(LearnProgress progress) {
        String key = key(progress.getUserId(), progress.getVideoId());
        Map<String, String> fields = new HashMap<>();
        fields.put("position", String.valueOf(progress.getPosition()));
        fields.put("duration", String.valueOf(progress.getDuration()));
        fields.put("speed", String.valueOf(progress.getSpeed()));
        fields.put("last_report_time", String.valueOf(progress.getLastReportTime()));
        fields.put("device_id", progress.getDeviceId() == null ? "" : progress.getDeviceId());
        redisTemplate.opsForHash().putAll(key, fields);
        redisTemplate.expire(key, Duration.ofSeconds(CommonConstants.PROGRESS_TTL_SECONDS));
    }

    private String key(Long userId, Long videoId) {
        return KEY_PREFIX + userId + ":" + videoId;
    }

    private Integer toInt(Object value) {
        return value == null ? null : Integer.valueOf(value.toString());
    }

    private Double toDouble(Object value) {
        return value == null ? null : Double.valueOf(value.toString());
    }

    private Long toLong(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }

    private String toStr(Object value) {
        return value == null ? null : value.toString();
    }
}
