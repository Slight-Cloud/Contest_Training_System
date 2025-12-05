package com.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.system.mapper")  // 指定 Mapper 接口所在包
public class ContestTrainingSystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(ContestTrainingSystemApplication.class, args);
    }

}
