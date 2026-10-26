package com.zixue.progress.anticheat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 防刷校验结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AntiCheatResult {

    /** 是否接受本次上报（false 表示拒绝，直接丢弃） */
    private boolean accepted;

    /** 本段是否计入有效时长 */
    private boolean countDuration;

    /** 与上次上报的间隔（秒），首次上报为 0 */
    private long intervalSeconds;

    /** 判定原因 */
    private AntiCheatReason reason;
}
