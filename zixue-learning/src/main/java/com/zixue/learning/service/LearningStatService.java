package com.zixue.learning.service;

import com.zixue.learning.entity.LearningStat;

/**
 * 学情聚合服务。
 */
public interface LearningStatService {

    /** 聚合某视频的学情指标 */
    void aggregate(Long videoId);

    /** 查询某视频最新学情指标 */
    LearningStat getStat(Long videoId);
}
