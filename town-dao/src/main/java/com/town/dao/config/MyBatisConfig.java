package com.town.dao.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.town.dao.mapper")
public class MyBatisConfig {
}

