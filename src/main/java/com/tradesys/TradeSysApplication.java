package com.tradesys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan("com.tradesys.mapper")
public class TradeSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeSysApplication.class, args);
        System.out.println("==========================================");
        System.out.println("   后台管理系统启动成功！");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("==========================================");
    }
}
