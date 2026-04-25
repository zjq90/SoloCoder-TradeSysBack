package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SysUserService extends IService<SysUser>, UserDetailsService {

    SysUser getByUsername(String username);
}
