package com.zixue.progress.dto;

import lombok.Data;

/**
 * 进度查询返回体（断点续播）。
 */
@Data
public class ProgressVO {

    private Long userId;

    private Long videoId;

    /** 播放位置（秒） */
    private Integer position;

    /** 视频总时长（秒） */
    private Integer duration;

    /** 最近倍速 */
    private Double speed;

    /** 最近上报时间戳（毫秒） */
    private Long lastReportTime;

    /** 最近活跃设备 */
    private String deviceId;
}
