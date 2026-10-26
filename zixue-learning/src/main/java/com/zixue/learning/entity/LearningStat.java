package com.zixue.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;

/**
 * 学情指标表（t_learning_stat）。
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatDim() {
        return statDim;
    }

    public void setStatDim(String statDim) {
        this.statDim = statDim;
    }

    public Long getDimId() {
        return dimId;
    }

    public void setDimId(Long dimId) {
        this.dimId = dimId;
    }

    public LocalDate getStatDate() {
        return statDate;
    }

    public void setStatDate(LocalDate statDate) {
        this.statDate = statDate;
    }

    public Integer getWatchUv() {
        return watchUv;
    }

    public void setWatchUv(Integer watchUv) {
        this.watchUv = watchUv;
    }

    public Double getFinishRate() {
        return finishRate;
    }

    public void setFinishRate(Double finishRate) {
        this.finishRate = finishRate;
    }

    public Integer getAvgValidDuration() {
        return avgValidDuration;
    }

    public void setAvgValidDuration(Integer avgValidDuration) {
        this.avgValidDuration = avgValidDuration;
    }

    public Double getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(Double bounceRate) {
        this.bounceRate = bounceRate;
    }
}
