package com.zixue.progress.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 进度上报请求体。
 *
 * <p>注意：上报时间戳由服务端生成（不信任客户端时钟），用于多端进度冲突裁决。</p>
 */
@Data
public class ProgressReportDTO {

    @NotNull(message = "userId 不能为空")
    private Long userId;

    @NotNull(message = "videoId 不能为空")
    private Long videoId;

    @NotNull(message = "position 不能为空")
    @Min(value = 0, message = "position 不能为负")
    private Integer position;

    @NotNull(message = "duration 不能为空")
    @Min(value = 0, message = "duration 不能为负")
    private Integer duration;

    @NotNull(message = "speed 不能为空")
    @DecimalMin(value = "0.5", message = "倍速不能小于 0.5")
    @DecimalMax(value = "2.0", message = "倍速不能大于 2.0")
    private Double speed = 1.0;

    @NotBlank(message = "deviceId 不能为空")
    private String deviceId;
}
