-- =============================================================
-- 知学学情平台 数据库初始化脚本
-- 由 docker-compose 启动 MySQL 时自动执行
-- =============================================================
CREATE DATABASE IF NOT EXISTS zixue DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE zixue;

-- 学习进度表
CREATE TABLE IF NOT EXISTS t_learn_progress (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT       NOT NULL COMMENT '用户ID',
    video_id         BIGINT       NOT NULL COMMENT '视频ID',
    position         INT          NOT NULL DEFAULT 0 COMMENT '播放位置(秒)',
    duration         INT          NOT NULL DEFAULT 0 COMMENT '视频总时长(秒)',
    speed            DECIMAL(3,1) NOT NULL DEFAULT 1.0 COMMENT '最近倍速',
    last_report_time BIGINT       NOT NULL COMMENT '最近上报时间戳(ms)',
    device_id        VARCHAR(64)  DEFAULT NULL COMMENT '最近活跃设备',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_video (user_id, video_id),
    KEY idx_video (video_id)
) COMMENT='学习进度表';

-- 有效时长统计表
CREATE TABLE IF NOT EXISTS t_learn_duration (
    id               BIGINT   AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT   NOT NULL COMMENT '用户ID',
    video_id         BIGINT   NOT NULL COMMENT '视频ID',
    valid_duration   INT      NOT NULL DEFAULT 0 COMMENT '累计有效时长(秒)',
    watch_count      INT      NOT NULL DEFAULT 0 COMMENT '观看次数',
    last_report_time BIGINT   NOT NULL DEFAULT 0 COMMENT '最近计入时长的上报时间戳(ms)',
    last_watch_time  DATETIME DEFAULT NULL COMMENT '最近观看时间',
    UNIQUE KEY uk_user_video (user_id, video_id),
    KEY idx_video (video_id)
) COMMENT='有效时长统计表';

-- 学情指标表
CREATE TABLE IF NOT EXISTS t_learning_stat (
    id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,
    stat_dim           VARCHAR(16)  NOT NULL COMMENT '维度: video/user',
    dim_id             BIGINT       NOT NULL COMMENT '维度ID',
    stat_date          DATE         NOT NULL COMMENT '统计日期',
    watch_uv           INT          NOT NULL DEFAULT 0 COMMENT '观看人数',
    finish_rate        DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '观看完成率%',
    avg_valid_duration INT          NOT NULL DEFAULT 0 COMMENT '平均有效时长(秒)',
    bounce_rate        DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '跳出率%',
    UNIQUE KEY uk_dim_date (stat_dim, dim_id, stat_date)
) COMMENT='学情指标表';
