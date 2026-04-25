package com.tradesys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.entity.Agent;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.mapper.SysRoleMapper;
import com.tradesys.mapper.SysUserMapper;
import com.tradesys.mapper.SysUserRoleMapper;
import com.tradesys.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AgentMapper agentMapper;

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public Page<SysUser> queryPage(PageQuery pageQuery, SysUser user) {
        Page<SysUser> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (user != null) {
            if (StringUtils.hasText(user.getUsername())) {
                wrapper.like(SysUser::getUsername, user.getUsername());
            }
            if (StringUtils.hasText(user.getRealName())) {
                wrapper.like(SysUser::getRealName, user.getRealName());
            }
            if (StringUtils.hasText(user.getPhone())) {
                wrapper.like(SysUser::getPhone, user.getPhone());
            }
            if (user.getStatus() != null) {
                wrapper.eq(SysUser::getStatus, user.getStatus());
            }
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        this.page(page, wrapper);
        
        fillAgentNames(page.getRecords());
        return page;
    }

    @Override
    public SysUser getDetailById(Long id) {
        SysUser user = this.getById(id);
        if (user != null) {
            List<SysRole> roles = baseMapper.selectRolesByUserId(id);
            user.setRoles(roles);
            List<SysPermission> permissions = baseMapper.selectPermissionsByUserId(id);
            user.setPermissions(permissions);
            
            if (user.getAgentId() != null) {
                Agent agent = agentMapper.selectById(user.getAgentId());
                if (agent != null) {
                    user.setAgentName(agent.getAgentName());
                }
            }
        }
        return user;
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
                        user.setAgentName(agent.getAgentName());
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithRoles(SysUser user, List<Long> roleIds) {
        boolean result = this.save(user);
        if (result && !CollectionUtils.isEmpty(roleIds)) {
            assignRoles(user.getId(), roleIds);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithRoles(SysUser user, List<Long> roleIds) {
        boolean result = this.updateById(user);
        if (result && roleIds != null) {
            assignRoles(user.getId(), roleIds);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWithRoles(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            return false;
        }
        assignRoles(id, new ArrayList<>());
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, String newPassword) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }

    @Override
    public boolean checkUsernameUnique(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        List<SysRole> roles = baseMapper.selectRolesByUserId(userId);
        return roles.stream().map(SysRole::getId).collect(Collectors.toList());
    }

    @Override
    public List<SysRole> getUserRoles(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.deleteByUserId(userId);
        if (!CollectionUtils.isEmpty(roleIds)) {
            sysUserRoleMapper.insertBatch(userId, roleIds);
        }
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
