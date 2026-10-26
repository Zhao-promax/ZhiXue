package com.zixue.progress.dto;

/**
 * 进度查询返回体（断点续播）。
 */
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Long getLastReportTime() {
        return lastReportTime;
    }

    public void setLastReportTime(Long lastReportTime) {
        this.lastReportTime = lastReportTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
