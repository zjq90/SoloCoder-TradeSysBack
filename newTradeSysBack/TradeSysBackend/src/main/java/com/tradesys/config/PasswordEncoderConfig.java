package com.tradesys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置类
 * 单独配置以避免循环依赖问题
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 密码编码器
     * 使用BCrypt加密算法
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
