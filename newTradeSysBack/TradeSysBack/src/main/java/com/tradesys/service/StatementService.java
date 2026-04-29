package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Statement;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

public interface StatementService extends IService<Statement> {

    Result<List<Statement>> getPage(PageQuery pageQuery, Statement statement,
                                       LocalDate startDate, LocalDate endDate);

    Result<Statement> getDetail(Long id);

    Result<List<Statement>> generateByDateRange(Long agentId, LocalDate startDate, LocalDate endDate);

    Result<Statement> generateByDate(Long agentId, LocalDate statDate);

    Result<Void> confirmStatement(Long id);

    Result<Void> raiseObjection(Long id, String remark);

    void exportExcel(Statement statement, LocalDate startDate, LocalDate endDate,
                     HttpServletResponse response);

    void exportStatementDetail(Long id, HttpServletResponse response);
}
