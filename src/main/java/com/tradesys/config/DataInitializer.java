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
    public void run(String... args) throws Exception {
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
            }
        }
    }
}
