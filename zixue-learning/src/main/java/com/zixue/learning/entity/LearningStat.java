package com.zixue.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学情指标表（t_learning_stat）。
 */
@Data
@TableName("t_learning_stat")
public class LearningStat {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 维度：video / user */
    private String statDim;

    /** 维度 ID（视频或用户 ID） */
    private Long dimId;

    /** 统计日期 */
    private LocalDate statDate;

    /** 观看人数 */
    private Integer watchUv;

    /** 观看完成率（%） */
    private Double finishRate;

    /** 平均有效时长（秒） */
    private Integer avgValidDuration;

    /** 跳出率（%） */
    private Double bounceRate;
}
