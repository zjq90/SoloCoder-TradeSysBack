package com.tradesys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.mapper.SysRoleMapper;
import com.tradesys.mapper.SysUserMapper;
import com.tradesys.mapper.SysUserRoleMapper;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统用户Service实现类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AgentMapper agentMapper;

    @Override
    public SysUser getByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        return sysUserMapper.selectByUsernameWithAgent(username);
    }

    @Override
    public Result<List<SysUser>> queryPage(PageQuery pageQuery, SysUser user) {
        Page<SysUser> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (user != null) {
            if (StrUtil.isNotBlank(user.getUsername())) {
                wrapper.like(SysUser::getUsername, user.getUsername());
            }
            if (StrUtil.isNotBlank(user.getRealName())) {
                wrapper.like(SysUser::getRealName, user.getRealName());
            }
            if (StrUtil.isNotBlank(user.getPhone())) {
                wrapper.like(SysUser::getPhone, user.getPhone());
            }
            if (user.getStatus() != null) {
                wrapper.eq(SysUser::getStatus, user.getStatus());
            }
        }

        wrapper.orderByDesc(SysUser::getCreateTime);
        this.page(page, wrapper);

        fillAgentNames(page.getRecords());

        return Result.success(page.getRecords(), page.getTotal());
    }

    @Override
    public Result<SysUser> getDetailById(Long id) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }

        SysUser user = this.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        List<SysRole> roles = sysUserMapper.selectRolesByUserId(id);
        user.setRoles(roles);

        List<SysPermission> permissions = sysUserMapper.selectPermissionsByUserId(id);
        user.setPermissions(permissions);

        if (user.getAgentId() != null) {
            Agent agent = agentMapper.selectById(user.getAgentId());
            if (agent != null) {
                user.setAgent(agent);
            }
        }

        return Result.success(user);
    }

    @Override
    public Result<Void> saveEntity(SysUser entity) {
        return Result.error("请使用saveWithRoles方法保存用户");
    }

    @Override
    public Result<Void> updateEntity(SysUser entity) {
        return Result.error("请使用updateWithRoles方法更新用户");
    }

    @Override
    public Result<Void> deleteById(Long id) {
        return deleteWithRoles(id);
    }

    @Override
    public Result<Void> deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.error("删除ID列表不能为空");
        }

        for (Long id : ids) {
            Result<Void> result = deleteWithRoles(id);
            if (!result.isSuccess()) {
                return result;
            }
        }

        return Result.ok();
    }

    @Override
    public Result<List<SysUser>> listAllActive() {
        List<SysUser> users = this.list(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 1)
                .orderByDesc(SysUser::getCreateTime));
        fillAgentNames(users);
        return Result.success(users);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveWithRoles(SysUser user, List<Long> roleIds) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }

        if (StrUtil.isBlank(user.getUsername())) {
            return Result.error("用户名不能为空");
        }

        if (!checkUsernameUnique(user.getUsername(), null)) {
            return Result.error("用户名已存在");
        }

        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode("123456"));
        }

        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        boolean saved = this.save(user);
        if (!saved) {
            return Result.error("保存用户失败");
        }

        if (!CollectionUtils.isEmpty(roleIds)) {
            assignRoles(user.getId(), roleIds);
        }

        log.info("新增用户成功: username={}", user.getUsername());
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateWithRoles(SysUser user, List<Long> roleIds) {
        if (user == null || user.getId() == null) {
            return Result.error("用户ID不能为空");
        }

        SysUser existUser = this.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }

        if (StrUtil.isNotBlank(user.getUsername())) {
            if (!checkUsernameUnique(user.getUsername(), user.getId())) {
                return Result.error("用户名已存在");
            }
        }

        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }

        boolean updated = this.updateById(user);
        if (!updated) {
            return Result.error("更新用户失败");
        }

        if (roleIds != null) {
            assignRoles(user.getId(), roleIds);
        }

        log.info("更新用户成功: userId={}", user.getId());
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteWithRoles(Long id) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }

        SysUser user = this.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        assignRoles(id, new ArrayList<>());
        this.removeById(id);

        log.info("删除用户成功: userId={}", id);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> resetPassword(Long id, String newPassword) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }

        if (StrUtil.isBlank(newPassword)) {
            return Result.error("新密码不能为空");
        }

        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));

        boolean updated = this.updateById(user);
        if (!updated) {
            return Result.error("重置密码失败");
        }

        log.info("重置密码成功: userId={}", id);
        return Result.ok();
    }

    @Override
    public boolean checkUsernameUnique(String username, Long excludeId) {
        if (StrUtil.isBlank(username)) {
            return false;
        }

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }

        return this.count(wrapper) == 0;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return sysUserRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public List<SysRole> getUserRoles(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        List<String> permissionCodes = new ArrayList<>();
        if (userId == null) {
            return permissionCodes;
        }

        List<SysPermission> permissions = sysUserMapper.selectPermissionsByUserId(userId);
        if (permissions != null && !permissions.isEmpty()) {
            for (SysPermission perm : permissions) {
                if (StrUtil.isNotBlank(perm.getPermissionCode())) {
                    permissionCodes.add(perm.getPermissionCode());
                }
            }
        }
        return permissionCodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null) {
            return;
        }

        sysUserRoleMapper.deleteByUserId(userId);
        if (!CollectionUtils.isEmpty(roleIds)) {
            sysUserRoleMapper.insertBatch(userId, roleIds);
        }

        log.info("分配角色成功: userId={}, roleIds={}", userId, roleIds);
    }

    @Override
    public Result<SysUser> getCurrentUser() {
        String username = SecurityUtils.getUsername();
        if (StrUtil.isBlank(username)) {
            return Result.error(401, "未登录");
        }

        SysUser user = getByUsername(username);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }

        List<SysRole> roles = getUserRoles(user.getId());
        user.setRoles(roles);

        List<String> permissions = getUserPermissions(user.getId());

        return Result.success(user);
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

        List<SysRole> roles = sysUserMapper.selectRolesByUserId(sysUser.getId());
        for (SysRole role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
        }

        List<SysPermission> permissions = sysUserMapper.selectPermissionsByUserId(sysUser.getId());
        for (SysPermission permission : permissions) {
            if (StrUtil.isNotBlank(permission.getPermissionCode())) {
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

    private void fillAgentNames(List<SysUser> users) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        Set<Long> agentIds = users.stream()
                .filter(u -> u.getAgentId() != null)
                .map(SysUser::getAgentId)
                .collect(Collectors.toSet());

        if (!agentIds.isEmpty()) {
            List<Agent> agents = agentMapper.selectBatchIds(agentIds);
            Map<Long, Agent> agentMap = agents.stream()
                    .collect(Collectors.toMap(Agent::getId, Function.identity()));

            for (SysUser user : users) {
                if (user.getAgentId() != null) {
                    Agent agent = agentMap.get(user.getAgentId());
                    if (agent != null) {
                        user.setAgent(agent);
                    }
                }
            }
        }
    }
}
