package com.zixue.learning.controller;

import com.zixue.common.result.Result;
import com.zixue.learning.entity.LearningStat;
import com.zixue.learning.service.LearningStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学情接口。
 */
@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningStatController {

    private final LearningStatService learningStatService;

    /** 查询某视频最新学情指标 */
    @GetMapping("/stats/{videoId}")
    public Result<LearningStat> getStat(@PathVariable Long videoId) {
        return Result.ok(learningStatService.getStat(videoId));
    }

    /** 手动触发某视频的学情聚合（便于演示） */
    @PostMapping("/stats/aggregate/{videoId}")
    public Result<Void> aggregate(@PathVariable Long videoId) {
        learningStatService.aggregate(videoId);
        return Result.ok();
    }
}
