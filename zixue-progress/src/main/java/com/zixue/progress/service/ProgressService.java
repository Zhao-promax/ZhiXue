package com.zixue.progress.service;

import com.zixue.common.dto.DurationSummaryVO;
import com.zixue.progress.dto.ProgressReportDTO;
import com.zixue.progress.dto.ProgressVO;

import java.util.List;

/**
 * 进度服务。
 */
public interface ProgressService {

    /** 进度上报（快路径） */
    void report(ProgressReportDTO dto);

    /** 断点续播查询 */
    ProgressVO getProgress(Long userId, Long videoId);

    /** 学情聚合内部接口：某视频的进度/时长汇总 */
    DurationSummaryVO getDurationSummary(Long videoId);

    /** 有进度记录的视频 ID 列表 */
    List<Long> listActiveVideoIds();
}
