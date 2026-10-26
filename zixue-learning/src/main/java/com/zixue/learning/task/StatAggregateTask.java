package com.zixue.learning.task;

import com.zixue.learning.feign.ProgressFeignClient;
import com.zixue.learning.lock.RedisLock;
import com.zixue.learning.service.LearningStatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 学情聚合定时任务：每小时执行一次，Redis 分布式锁保证多实例下单次执行。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatAggregateTask {

    private static final String AGGREGATE_LOCK = "learning:aggregate";

    private final RedisLock redisLock;
    private final ProgressFeignClient progressFeignClient;
    private final LearningStatService learningStatService;

    @Scheduled(cron = "0 0 * * * ?")
    public void aggregate() {
        if (!redisLock.tryLock(AGGREGATE_LOCK, 60)) {
            log.debug("未获取到分布式锁，跳过本次聚合");
            return;
        }
        try {
            List<Long> videoIds = progressFeignClient.getActiveVideoIds().getData();
            if (videoIds == null || videoIds.isEmpty()) {
                return;
            }
            for (Long videoId : videoIds) {
                learningStatService.aggregate(videoId);
            }
        } finally {
            redisLock.unlock(AGGREGATE_LOCK);
        }
    }
}
