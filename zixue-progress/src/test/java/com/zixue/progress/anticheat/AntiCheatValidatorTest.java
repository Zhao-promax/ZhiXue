package com.zixue.progress.anticheat;

import com.zixue.progress.dto.ProgressReportDTO;
import com.zixue.progress.entity.LearnProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 三重防刷校验单元测试。
 */
class AntiCheatValidatorTest {

    private final AntiCheatValidator validator = new AntiCheatValidator();

    private ProgressReportDTO report(int position, double speed, String deviceId) {
        ProgressReportDTO dto = new ProgressReportDTO();
        dto.setUserId(1L);
        dto.setVideoId(2L);
        dto.setPosition(position);
        dto.setDuration(3600);
        dto.setSpeed(speed);
        dto.setDeviceId(deviceId);
        return dto;
    }

    private LearnProgress previous(long lastReportTime, String deviceId) {
        LearnProgress p = new LearnProgress();
        p.setUserId(1L);
        p.setVideoId(2L);
        p.setPosition(0);
        p.setDuration(3600);
        p.setSpeed(1.0);
        p.setLastReportTime(lastReportTime);
        p.setDeviceId(deviceId);
        return p;
    }

    @Test
    @DisplayName("首次上报应被接受")
    void firstReport_shouldBeAccepted() {
        AntiCheatResult result = validator.validate(report(0, 1.0, "dev1"), null, 100_000L);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.getIntervalSeconds()).isZero();
    }

    @Test
    @DisplayName("间隔小于 3 秒应拒绝")
    void intervalLessThan3s_shouldReject() {
        LearnProgress prev = previous(100_000L, "dev1");
        AntiCheatResult result = validator.validate(report(10, 1.0, "dev1"), prev, 102_000L);
        assertThat(result.isAccepted()).isFalse();
        assertThat(result.getReason()).isEqualTo(AntiCheatReason.TOO_FREQUENT);
    }

    @Test
    @DisplayName("间隔正常应计入时长")
    void normalInterval_shouldCountDuration() {
        LearnProgress prev = previous(100_000L, "dev1");
        AntiCheatResult result = validator.validate(report(10, 1.0, "dev1"), prev, 110_000L);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.isCountDuration()).isTrue();
        assertThat(result.getIntervalSeconds()).isEqualTo(10);
    }

    @Test
    @DisplayName("间隔超过 120 秒判断档，不计时长")
    void intervalGreaterThan120s_shouldNotCount() {
        LearnProgress prev = previous(100_000L, "dev1");
        AntiCheatResult result = validator.validate(report(10, 1.0, "dev1"), prev, 100_000L + 121_000L);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.isCountDuration()).isFalse();
        assertThat(result.getReason()).isEqualTo(AntiCheatReason.HEARTBEAT_GAP);
    }

    @Test
    @DisplayName("倍速超范围不计时长")
    void speedOutOfRange_shouldNotCount() {
        LearnProgress prev = previous(100_000L, "dev1");
        AntiCheatResult result = validator.validate(report(10, 3.0, "dev1"), prev, 110_000L);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.isCountDuration()).isFalse();
        assertThat(result.getReason()).isEqualTo(AntiCheatReason.SPEED_INVALID);
    }

    @Test
    @DisplayName("多端切换设备不计时长")
    void deviceSwitch_shouldNotCount() {
        LearnProgress prev = previous(100_000L, "dev1");
        AntiCheatResult result = validator.validate(report(10, 1.0, "dev2"), prev, 110_000L);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.isCountDuration()).isFalse();
        assertThat(result.getReason()).isEqualTo(AntiCheatReason.DEVICE_SWITCH);
    }
}
