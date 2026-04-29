package com.tradesys.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradesys.entity.SysUser;
import com.tradesys.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 在应用启动时执行数据初始化操作
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            log.info("开始执行数据初始化...");
            
            SysUser admin = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, "admin")
            );
            
            if (admin != null) {
                String rawPassword = "123456";
                if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
                    String encodedPassword = passwordEncoder.encode(rawPassword);
                    admin.setPassword(encodedPassword);
                    sysUserMapper.updateById(admin);
                    log.info("========================================");
                    log.info("已重置 admin 用户密码: 123456");
                    log.info("========================================");
                } else {
                    log.info("admin 用户密码已正确，无需重置");
                }
            } else {
                log.warn("未找到 admin 用户，跳过密码重置");
            }
            
            log.info("数据初始化完成");
        } catch (Exception e) {
            log.error("========================================");
            log.error("数据初始化失败: {}", e.getMessage());
            log.error("请检查数据库连接是否正常");
            log.error("========================================", e);
        }
    }
}
