package com.zixue.progress.anticheat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 有效时长计算单元测试。
 */
class DurationCalculatorTest {

    private final DurationCalculator calculator = new DurationCalculator();

    @Test
    @DisplayName("正常观看应返回间隔时长")
    void normalWatch_shouldReturnInterval() {
        assertThat(calculator.calcValidDurationSeconds(1.0, 10, 10)).isEqualTo(10);
    }

    @Test
    @DisplayName("二倍速正常观看应按真实间隔计时")
    void doubleSpeedNormalWatch_shouldReturnInterval() {
        assertThat(calculator.calcValidDurationSeconds(2.0, 10, 20)).isEqualTo(10);
    }

    @Test
    @DisplayName("向前拖动应返回 0")
    void seekForward_shouldReturnZero() {
        assertThat(calculator.calcValidDurationSeconds(1.0, 10, 100)).isZero();
    }

    @Test
    @DisplayName("回退应返回 0")
    void rewind_shouldReturnZero() {
        assertThat(calculator.calcValidDurationSeconds(1.0, 10, -5)).isZero();
    }

    @Test
    @DisplayName("暂停（位置不变）应返回 0")
    void pause_shouldReturnZero() {
        assertThat(calculator.calcValidDurationSeconds(1.0, 10, 0)).isZero();
    }

    @Test
    @DisplayName("间隔非正应返回 0")
    void zeroInterval_shouldReturnZero() {
        assertThat(calculator.calcValidDurationSeconds(1.0, 0, 0)).isZero();
    }
}
