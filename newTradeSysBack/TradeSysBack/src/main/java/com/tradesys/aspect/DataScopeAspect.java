package com.tradesys.aspect;

import com.tradesys.annotation.DataScope;
import com.tradesys.entity.SysUser;
import com.tradesys.service.SysUserService;
import com.tradesys.util.DataScopeContext;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    private final SysUserService sysUserService;

    public static final String DATA_SCOPE_ALL = "1";
    public static final String DATA_SCOPE_CUSTOM = "2";
    public static final String DATA_SCOPE_DEPT = "3";
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";
    public static final String DATA_SCOPE_SELF = "5";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) {
        if (SecurityUtils.isAdmin()) {
            log.debug("当前用户是管理员，不进行数据权限过滤");
            return;
        }

        String username = SecurityUtils.getUsername();
        if (username == null) {
            log.warn("无法获取当前用户信息");
            return;
        }

        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            return;
        }

        StringBuilder sqlString = new StringBuilder();
        String agentAlias = controllerDataScope.agentAlias();

        sqlString.append(String.format(" (%s.id = %d OR %s.parent_path LIKE CONCAT('%%/', %d, '/%%'))",
                agentAlias, user.getAgentId(), agentAlias, user.getAgentId()));

        log.debug("数据权限过滤条件: {}", sqlString);
        DataScopeContext.set(sqlString.toString());
    }

    @After("@annotation(controllerDataScope)")
    public void doAfter(JoinPoint point, DataScope controllerDataScope) {
        DataScopeContext.remove();
    }
}
