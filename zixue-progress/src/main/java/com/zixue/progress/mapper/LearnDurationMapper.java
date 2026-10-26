package com.zixue.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixue.progress.entity.LearnDuration;
import org.apache.ibatis.annotations.Param;

/**
 * 有效时长 Mapper。
 */
public interface LearnDurationMapper extends BaseMapper<LearnDuration> {

    /**
     * 累加有效时长（按 reportTime 幂等，重复/乱序消息不重复累加）。
     */
    int accumulate(@Param("userId") Long userId,
                   @Param("videoId") Long videoId,
                   @Param("validSeconds") Long validSeconds,
                   @Param("reportTime") Long reportTime);

    /**
     * 查询某视频的总有效时长（秒）。
     */
    Long selectTotalValidDuration(@Param("videoId") Long videoId);
}
