package com.zixue.common.dto;

/**
 * 学情聚合内部接口返回：某视频的进度/时长汇总。
 * 由进度服务提供，学情服务通过 OpenFeign 调用（跨服务共享 DTO）。
 */
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

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Integer getWatchUv() {
        return watchUv;
    }

    public void setWatchUv(Integer watchUv) {
        this.watchUv = watchUv;
    }

    public Integer getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(Integer finishCount) {
        this.finishCount = finishCount;
    }

    public Integer getBounceCount() {
        return bounceCount;
    }

    public void setBounceCount(Integer bounceCount) {
        this.bounceCount = bounceCount;
    }

    public Long getTotalValidDuration() {
        return totalValidDuration;
    }

    public void setTotalValidDuration(Long totalValidDuration) {
        this.totalValidDuration = totalValidDuration;
    }
}
