package com.town.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.town.mapper")
public class MyBatisConfig {
}

