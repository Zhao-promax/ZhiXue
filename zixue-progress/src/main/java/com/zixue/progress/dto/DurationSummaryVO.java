package com.zixue.progress.dto;

import lombok.Data;

/**
 * 学情聚合内部接口返回：某视频的进度/时长汇总。
 * 由进度服务提供，学情服务通过 OpenFeign 调用。
 */
@Data
public class DurationSummaryVO {

    private Long videoId;

    /** 观看人数 */
    private Integer watchUv;

    /** 完播人数（观看进度 >= 90%） */
    private Integer finishCount;

    /** 跳出人数（观看进度 < 10%） */
    private Integer bounceCount;

    /** 总有效时长（秒） */
    private Long totalValidDuration;
}
