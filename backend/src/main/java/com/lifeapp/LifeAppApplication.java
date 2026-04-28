package com.lifeapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lifeapp.mapper")
@SpringBootApplication
public class LifeAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeAppApplication.class, args);
    }
}
