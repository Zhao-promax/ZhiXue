package com.zixue.progress.anticheat;

import com.zixue.progress.dto.ProgressReportDTO;
import com.zixue.progress.entity.LearnProgress;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 三重防刷校验（纯逻辑，可单测）：
 * <ol>
 *   <li>心跳间隔校验：间隔 &lt; 3s 拒绝，&gt; 120s 判断档；</li>
 *   <li>倍速校验：倍速不在 [0.5, 2.0] 区间不计时长；</li>
 *   <li>多端去重：与上次活跃设备不一致时不计时长。</li>
 * </ol>
 */
@Component
public class AntiCheatValidator {

    /** 心跳最小间隔（秒） */
    public static final long MIN_HEARTBEAT_INTERVAL_SECONDS = 3;

    /** 心跳最大间隔（秒），超过视为断档 */
    public static final long MAX_HEARTBEAT_INTERVAL_SECONDS = 120;

    /** 允许的最小倍速 */
    public static final double MIN_SPEED = 0.5;

    /** 允许的最大倍速 */
    public static final double MAX_SPEED = 2.0;

    /**
     * @param report            本次上报
     * @param previous          上次进度（Redis/DB 中的快照），首次上报为 null
     * @param currentTimeMillis 服务端当前时间戳
     */
    public AntiCheatResult validate(ProgressReportDTO report, LearnProgress previous, long currentTimeMillis) {
        if (previous == null) {
            boolean speedOk = isSpeedValid(report.getSpeed());
            return new AntiCheatResult(true, speedOk, 0,
                    speedOk ? AntiCheatReason.NONE : AntiCheatReason.SPEED_INVALID);
        }

        long intervalSeconds = (currentTimeMillis - previous.getLastReportTime()) / 1000;

        // 心跳过频：直接拒绝
        if (intervalSeconds < MIN_HEARTBEAT_INTERVAL_SECONDS) {
            return new AntiCheatResult(false, false, intervalSeconds, AntiCheatReason.TOO_FREQUENT);
        }

        boolean countDuration = true;
        AntiCheatReason reason = AntiCheatReason.NONE;

        if (!isSpeedValid(report.getSpeed())) {
            countDuration = false;
            reason = AntiCheatReason.SPEED_INVALID;
        }
        if (intervalSeconds > MAX_HEARTBEAT_INTERVAL_SECONDS) {
            countDuration = false;
            reason = AntiCheatReason.HEARTBEAT_GAP;
        }
        if (!Objects.equals(report.getDeviceId(), previous.getDeviceId())) {
            countDuration = false;
            reason = AntiCheatReason.DEVICE_SWITCH;
        }

        return new AntiCheatResult(true, countDuration, intervalSeconds, reason);
    }

    public boolean isSpeedValid(double speed) {
        return speed >= MIN_SPEED && speed <= MAX_SPEED;
    }
}
