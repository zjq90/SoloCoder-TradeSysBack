package com.tradesys.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询参数类
 * 用于统一处理分页查询的请求参数
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页记录数
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页记录数
     */
    private static final int MAX_PAGE_SIZE = 1000;

    /**
     * 页码
     */
    private Integer pageNum = DEFAULT_PAGE_NUM;

    /**
     * 每页记录数
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式：asc/desc
     */
    private String orderType;

    /**
     * 获取页码（带默认值和边界检查）
     *
     * @return 页码
     */
    public Integer getPageNum() {
        if (pageNum == null || pageNum < 1) {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    /**
     * 设置页码
     *
     * @param pageNum 页码
     */
    public void setPageNum(Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            this.pageNum = DEFAULT_PAGE_NUM;
        } else {
            this.pageNum = pageNum;
        }
    }

    /**
     * 获取每页记录数（带默认值和边界检查）
     *
     * @return 每页记录数
     */
    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    /**
     * 设置每页记录数
     *
     * @param pageSize 每页记录数
     */
    public void setPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else if (pageSize > MAX_PAGE_SIZE) {
            this.pageSize = MAX_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }
    }

    /**
     * 获取起始偏移量
     *
     * @return 偏移量
     */
    public long getOffset() {
        return (long) (getPageNum() - 1) * getPageSize();
    }

    /**
     * 构建分页查询对象
     *
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @return PageQuery对象
     */
    public static PageQuery of(Integer pageNum, Integer pageSize) {
        PageQuery query = new PageQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return query;
    }

    /**
     * 构建分页查询对象（带排序）
     *
     * @param pageNum   页码
     * @param pageSize  每页记录数
     * @param orderBy   排序字段
     * @param orderType 排序方式
     * @return PageQuery对象
     */
    public static PageQuery of(Integer pageNum, Integer pageSize, String orderBy, String orderType) {
        PageQuery query = of(pageNum, pageSize);
        query.setOrderBy(orderBy);
        query.setOrderType(orderType);
        return query;
    }
}
