package com.zixue.progress.anticheat;

/**
 * 防刷校验结果。
 */
public class AntiCheatResult {

    /** 是否接受本次上报（false 表示拒绝，直接丢弃） */
    private boolean accepted;

    /** 本段是否计入有效时长 */
    private boolean countDuration;

    /** 与上次上报的间隔（秒），首次上报为 0 */
    private long intervalSeconds;

    /** 判定原因 */
    private AntiCheatReason reason;

    public AntiCheatResult() {
    }

    public AntiCheatResult(boolean accepted, boolean countDuration, long intervalSeconds, AntiCheatReason reason) {
        this.accepted = accepted;
        this.countDuration = countDuration;
        this.intervalSeconds = intervalSeconds;
        this.reason = reason;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isCountDuration() {
        return countDuration;
    }

    public void setCountDuration(boolean countDuration) {
        this.countDuration = countDuration;
    }

    public long getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public AntiCheatReason getReason() {
        return reason;
    }

    public void setReason(AntiCheatReason reason) {
        this.reason = reason;
    }
}
