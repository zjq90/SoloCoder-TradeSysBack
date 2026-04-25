package com.tradesys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.mapper.SysUserMapper;
import com.tradesys.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = getByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (sysUser.getStatus() == null || sysUser.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已禁用: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        List<SysRole> roles = baseMapper.selectRolesByUserId(sysUser.getId());
        for (SysRole role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
        }

        List<SysPermission> permissions = baseMapper.selectPermissionsByUserId(sysUser.getId());
        for (SysPermission permission : permissions) {
            if (permission.getPermissionCode() != null && !permission.getPermissionCode().isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionCode()));
            }
        }

        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                true,
                true,
                true,
                true,
                authorities
        );
    }
}
