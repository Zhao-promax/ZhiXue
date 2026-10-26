package com.zixue.learning.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 基于 Redis 的简单分布式锁（SET NX EX），保证定时任务在多实例下单次执行。
 */
@Component
public class RedisLock {

    private static final String LOCK_PREFIX = "lock:";

    private final StringRedisTemplate redisTemplate;

    public RedisLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试加锁，返回是否获取成功。
     */
    public boolean tryLock(String name, long ttlSeconds) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(LOCK_PREFIX + name, "1", Duration.ofSeconds(ttlSeconds)));
    }

    /**
     * 释放锁。
     */
    public void unlock(String name) {
        redisTemplate.delete(LOCK_PREFIX + name);
    }
}
