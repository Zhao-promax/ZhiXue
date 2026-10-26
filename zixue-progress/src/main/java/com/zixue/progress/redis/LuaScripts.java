package com.zixue.progress.redis;

/**
 * Redis Lua 脚本常量。
 */
public final class LuaScripts {

    private LuaScripts() {
    }

    /**
     * 更新进度（原子，按上报时间戳裁决多端并发写冲突）。
     *
     * <p>KEYS[1]：进度 key；ARGV[1..6]：position / duration / speed / reportTime / deviceId / ttl。</p>
     * <p>仅当新上报时间戳 ≥ 已存时间戳时才覆盖（last-write-wins），否则返回 0 拒绝旧数据。</p>
     */
    public static final String UPDATE_PROGRESS = """
            local last = redis.call('HGET', KEYS[1], 'last_report_time')
            if (not last) or (tonumber(ARGV[4]) >= tonumber(last)) then
              redis.call('HSET', KEYS[1],
                'position', ARGV[1],
                'duration', ARGV[2],
                'speed', ARGV[3],
                'last_report_time', ARGV[4],
                'device_id', ARGV[5])
              redis.call('EXPIRE', KEYS[1], ARGV[6])
              return 1
            else
              return 0
            end
            """;
}
