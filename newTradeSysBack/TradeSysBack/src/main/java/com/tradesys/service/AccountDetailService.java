package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.AccountDetail;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AccountDetailService extends IService<AccountDetail> {

    Result<List<AccountDetail>> getPage(PageQuery pageQuery, AccountDetail detail,
                                          LocalDate startDate, LocalDate endDate);

    Result<AccountDetail> getDetail(Long id);

    Result<List<Map<String, Object>>> getStatsByAgent(Long agentId, LocalDate startDate, LocalDate endDate);

    void exportExcel(AccountDetail detail, LocalDate startDate, LocalDate endDate,
                     HttpServletResponse response);
}
