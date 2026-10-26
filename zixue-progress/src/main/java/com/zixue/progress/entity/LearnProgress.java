package com.zixue.progress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习进度表（t_learn_progress）。
 */
@Data
@TableName("t_learn_progress")
public class LearnProgress {

    @TableId(type = IdType.AUTO)
    private Long id;

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

    private LocalDateTime updateTime;
}
