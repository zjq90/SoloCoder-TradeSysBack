package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Transaction;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionService extends IService<Transaction> {

    Result<List<Transaction>> getPage(PageQuery pageQuery, Transaction transaction,
                                        LocalDate startDate, LocalDate endDate);

    Result<Transaction> getDetail(Long id);

    Result<List<Map<String, Object>>> getDailyStats(LocalDate startDate, LocalDate endDate);

    Result<Map<String, Object>> getOverviewStats();

    void exportExcel(Transaction transaction, LocalDate startDate, LocalDate endDate,
                     HttpServletResponse response);
}
