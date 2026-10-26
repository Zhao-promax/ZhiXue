package com.zixue.common.constant;

/**
 * 公共常量。
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /** 网关透传的用户 ID 请求头 */
    public static final String USER_ID_HEADER = "X-User-Id";

    /** 学习进度 Redis 缓存过期时间：30 天（秒） */
    public static final long PROGRESS_TTL_SECONDS = 30L * 24 * 60 * 60;
}
