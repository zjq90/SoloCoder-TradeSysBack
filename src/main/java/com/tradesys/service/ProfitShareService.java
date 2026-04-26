package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.ProfitShare;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ProfitShareService extends IService<ProfitShare> {

    Result<List<ProfitShare>> getPage(PageQuery pageQuery, ProfitShare profit, 
                                       LocalDate startDate, LocalDate endDate);

    Result<ProfitShare> getDetail(Long id);

    Result<Void> settleProfit(Long id);

    Result<Void> batchSettle(List<Long> ids);

    Result<List<Map<String, Object>>> getStatsByAgent(Long agentId, LocalDate startDate, LocalDate endDate);

    void exportExcel(ProfitShare profit, LocalDate startDate, LocalDate endDate, 
                     HttpServletResponse response);
}
