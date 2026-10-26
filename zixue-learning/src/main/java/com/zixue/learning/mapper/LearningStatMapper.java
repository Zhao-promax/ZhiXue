package com.zixue.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixue.learning.entity.LearningStat;
import org.apache.ibatis.annotations.Param;

/**
 * 学情指标 Mapper。
 */
public interface LearningStatMapper extends BaseMapper<LearningStat> {

    /**
     * upsert 学情指标（按 stat_dim + dim_id + stat_date 幂等）。
     */
    int upsert(LearningStat stat);

    /**
     * 查询最新一条学情指标。
     */
    LearningStat selectLatest(@Param("statDim") String statDim, @Param("dimId") Long dimId);
}
