package com.zixue.learning.feign;

import com.zixue.common.dto.DurationSummaryVO;
import com.zixue.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 进度服务 OpenFeign 客户端：学情服务跨服务调用进度服务。
 */
@FeignClient(name = "zixue-progress", path = "/api/progress")
public interface ProgressFeignClient {

    /** 某视频的进度/时长汇总 */
    @GetMapping("/internal/valid-duration/summary")
    Result<DurationSummaryVO> getDurationSummary(@RequestParam("videoId") Long videoId);

    /** 有进度记录的视频 ID 列表 */
    @GetMapping("/internal/video-ids")
    Result<List<Long>> getActiveVideoIds();
}
