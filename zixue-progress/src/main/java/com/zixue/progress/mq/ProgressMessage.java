package com.zixue.progress.mq;

/**
 * 进度上报 MQ 消息体（生产者 → 消费者）。
 */
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

    public ProgressMessage() {
    }

    public ProgressMessage(Long userId, Long videoId, Integer position, Integer duration,
                           Double speed, Long reportTime, String deviceId, Long validSeconds) {
        this.userId = userId;
        this.videoId = videoId;
        this.position = position;
        this.duration = duration;
        this.speed = speed;
        this.reportTime = reportTime;
        this.deviceId = deviceId;
        this.validSeconds = validSeconds;
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

    public Long getReportTime() {
        return reportTime;
    }

    public void setReportTime(Long reportTime) {
        this.reportTime = reportTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getValidSeconds() {
        return validSeconds;
    }

    public void setValidSeconds(Long validSeconds) {
        this.validSeconds = validSeconds;
    }
}
