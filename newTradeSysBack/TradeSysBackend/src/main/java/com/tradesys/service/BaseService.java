package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;

import java.util.List;

/**
 * 基础Service接口
 * 定义通用的CRUD操作
 *
 * @param <T> 实体类型
 * @author TradeSys Team
 * @version 1.0.0
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 分页查询
     *
     * @param pageQuery 分页参数
     * @param entity    查询条件实体
     * @return 分页结果
     */
    Result<List<T>> queryPage(PageQuery pageQuery, T entity);

    /**
     * 根据ID查询详情
     *
     * @param id 主键ID
     * @return 实体详情
     */
    Result<T> getDetailById(Long id);

    /**
     * 新增实体
     *
     * @param entity 实体对象
     * @return 操作结果
     */
    Result<Void> saveEntity(T entity);

    /**
     * 更新实体
     *
     * @param entity 实体对象
     * @return 操作结果
     */
    Result<Void> updateEntity(T entity);

    /**
     * 根据ID删除实体
     *
     * @param id 主键ID
     * @return 操作结果
     */
    Result<Void> deleteById(Long id);

    /**
     * 批量删除实体
     *
     * @param ids 主键ID列表
     * @return 操作结果
     */
    Result<Void> deleteByIds(List<Long> ids);

    /**
     * 查询所有启用的实体
     *
     * @return 实体列表
     */
    Result<List<T>> listAllActive();
}
