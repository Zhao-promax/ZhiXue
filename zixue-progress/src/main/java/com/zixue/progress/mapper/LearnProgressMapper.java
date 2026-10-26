package com.zixue.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixue.common.dto.DurationSummaryVO;
import com.zixue.progress.entity.LearnProgress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习进度 Mapper。
 */
public interface LearnProgressMapper extends BaseMapper<LearnProgress> {

    /**
     * 批量 upsert（ON DUPLICATE KEY UPDATE），保证上报幂等。
     */
    int batchUpsert(@Param("list") List<LearnProgress> list);

    /**
     * 查询某视频的观看人数 / 完播数 / 跳出数（供学情聚合）。
     */
    DurationSummaryVO selectProgressStats(@Param("videoId") Long videoId);

    /**
     * 查询有进度记录的视频 ID 列表（供学情服务定时聚合）。
     */
    List<Long> selectActiveVideoIds();
}
