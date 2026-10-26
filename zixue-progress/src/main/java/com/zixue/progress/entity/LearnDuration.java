package com.zixue.progress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 有效时长统计表（t_learn_duration）。
 */
@TableName("t_learn_duration")
public class LearnDuration {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long videoId;

    /** 累计有效时长（秒） */
    private Integer validDuration;

    /** 观看次数 */
    private Integer watchCount;

    /** 最近一次计入时长的上报时间戳（毫秒），用于幂等去重 */
    private Long lastReportTime;

    private LocalDateTime lastWatchTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Integer getValidDuration() {
        return validDuration;
    }

    public void setValidDuration(Integer validDuration) {
        this.validDuration = validDuration;
    }

    public Integer getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    public Long getLastReportTime() {
        return lastReportTime;
    }

    public void setLastReportTime(Long lastReportTime) {
        this.lastReportTime = lastReportTime;
    }

    public LocalDateTime getLastWatchTime() {
        return lastWatchTime;
    }

    public void setLastWatchTime(LocalDateTime lastWatchTime) {
        this.lastWatchTime = lastWatchTime;
    }
}
