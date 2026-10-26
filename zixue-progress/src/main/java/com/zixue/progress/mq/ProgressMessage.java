package com.zixue.progress.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进度上报 MQ 消息体（生产者 → 消费者）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressMessage {

    private Long userId;

    private Long videoId;

    /** 播放位置（秒） */
    private Integer position;

    /** 视频总时长（秒） */
    private Integer duration;

    /** 倍速 */
    private Double speed;

    /** 服务端生成的上报时间戳（毫秒） */
    private Long reportTime;

    /** 设备 ID */
    private String deviceId;

    /** 本次计入的有效时长（秒），0 表示该段不计时长（断档/拖动/切设备等） */
    private Long validSeconds;
}
