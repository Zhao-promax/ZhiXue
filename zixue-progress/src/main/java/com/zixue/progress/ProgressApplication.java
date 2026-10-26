package com.zixue.progress;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 进度上报服务启动类。
 */
@EnableScheduling
@MapperScan("com.zixue.progress.mapper")
@SpringBootApplication
public class ProgressApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgressApplication.class, args);
    }
}
