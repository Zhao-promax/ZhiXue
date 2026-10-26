package com.zixue.progress.anticheat;

import org.springframework.stereotype.Component;

/**
 * 有效时长计算（纯逻辑，可单测）。
 *
 * <p>有效时长按相邻两次心跳的时间差累加；通过位置增量识别「拖动/回退」，
 * 位置增量明显偏离 speed × interval 时判定为拖动，该段不计时长。</p>
 */
@Component
public class DurationCalculator {

    /**
     * @param speed           倍速
     * @param intervalSeconds 两次心跳间隔（秒）
     * @param positionDelta   位置增量（秒），= 本次 position - 上次 position
     * @return 计入的有效时长（秒），拖动/回退返回 0
     */
    public long calcValidDurationSeconds(double speed, long intervalSeconds, int positionDelta) {
        if (intervalSeconds <= 0) {
            return 0;
        }
        long expectedDelta = (long) (speed * intervalSeconds);
        long tolerance = Math.max(3, expectedDelta / 5);
        long lower = Math.max(0, expectedDelta - tolerance);
        long upper = expectedDelta + tolerance;

        if (positionDelta < lower || positionDelta > upper) {
            // 位置跳变：拖动进度条或回退
            return 0;
        }
        return intervalSeconds;
    }
}
