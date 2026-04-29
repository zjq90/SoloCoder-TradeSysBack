package com.tradesys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 交易管理系统后端启动类
 * 
 * @author TradeSys Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@MapperScan("com.tradesys.mapper")
public class TradeSysBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeSysBackendApplication.class, args);
        System.out.println("==========================================");
        System.out.println("   交易管理系统后端启动成功！");
        System.out.println("   访问地址: http://localhost:8081");
        System.out.println("   H2控制台: http://localhost:8081/h2-console");
        System.out.println("   Druid监控: http://localhost:8081/druid");
        System.out.println("==========================================");
    }
}
