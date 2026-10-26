package com.zixue.progress.anticheat;

/**
 * 防刷判定原因。
 */
public enum AntiCheatReason {

    /** 正常 */
    NONE,

    /** 心跳过频（间隔 < 3s），拒绝本次上报 */
    TOO_FREQUENT,

    /** 倍速异常（< 0.5 或 > 2.0），不计时长 */
    SPEED_INVALID,

    /** 心跳断档（间隔 > 120s），不计时长 */
    HEARTBEAT_GAP,

    /** 多端切换设备，不计时长 */
    DEVICE_SWITCH
}
