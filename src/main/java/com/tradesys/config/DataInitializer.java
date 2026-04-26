package com.tradesys.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradesys.entity.SysUser;
import com.tradesys.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            System.out.println("开始执行数据初始化...");
            SysUser admin = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
            
            if (admin != null) {
                String rawPassword = "123456";
                if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
                    String encodedPassword = passwordEncoder.encode(rawPassword);
                    admin.setPassword(encodedPassword);
                    sysUserMapper.updateById(admin);
                    System.out.println("========================================");
                    System.out.println("已重置 admin 用户密码: 123456");
                    System.out.println("========================================");
                } else {
                    System.out.println("admin 用户密码已正确，无需重置");
                }
            } else {
                System.out.println("未找到 admin 用户，跳过密码重置");
            }
            System.out.println("数据初始化完成");
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("数据初始化失败: " + e.getMessage());
            System.err.println("请检查数据库连接是否正常，数据库是否已创建");
            System.err.println("========================================");
            e.printStackTrace();
        }
    }
}
