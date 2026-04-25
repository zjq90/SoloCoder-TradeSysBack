package com.tradesys.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private String orderType = "desc";

    public Integer getPageNum() {
        return pageNum != null && pageNum > 0 ? pageNum : 1;
    }

    public Integer getPageSize() {
        return pageSize != null && pageSize > 0 ? pageSize : 10;
    }
}
