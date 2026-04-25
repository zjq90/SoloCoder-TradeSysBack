package com.tradesys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface SysUserService extends IService<SysUser>, UserDetailsService {

    SysUser getByUsername(String username);

    Page<SysUser> queryPage(PageQuery pageQuery, SysUser user);

    SysUser getDetailById(Long id);

    boolean saveWithRoles(SysUser user, List<Long> roleIds);

    boolean updateWithRoles(SysUser user, List<Long> roleIds);

    boolean deleteWithRoles(Long id);

    boolean resetPassword(Long id, String newPassword);

    boolean checkUsernameUnique(String username, Long excludeId);

    List<Long> getUserRoleIds(Long userId);

    List<SysRole> getUserRoles(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);
}
