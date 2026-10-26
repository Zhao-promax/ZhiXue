package com.zixue.progress.controller;

import com.zixue.common.constant.CommonConstants;
import com.zixue.common.result.Result;
import com.zixue.progress.dto.DurationSummaryVO;
import com.zixue.progress.dto.ProgressReportDTO;
import com.zixue.progress.dto.ProgressVO;
import com.zixue.progress.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 进度上报接口。
 */
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    /** 进度上报（高频心跳） */
    @PostMapping("/report")
    public Result<Void> report(@Validated @RequestBody ProgressReportDTO dto) {
        progressService.report(dto);
        return Result.ok();
    }

    /** 断点续播查询 */
    @GetMapping("/{videoId}")
    public Result<ProgressVO> getProgress(@RequestHeader(CommonConstants.USER_ID_HEADER) Long userId,
                                          @PathVariable Long videoId) {
        return Result.ok(progressService.getProgress(userId, videoId));
    }

    /** 内部接口：学情聚合用（学情服务 OpenFeign 调用） */
    @GetMapping("/internal/valid-duration/summary")
    public Result<DurationSummaryVO> getDurationSummary(@RequestParam Long videoId) {
        return Result.ok(progressService.getDurationSummary(videoId));
    }
}
