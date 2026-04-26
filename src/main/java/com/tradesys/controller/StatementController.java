package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Statement;
import com.tradesys.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;

    @GetMapping("/api/list")
    public Result<List<Statement>> list(PageQuery pageQuery, Statement statement,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return statementService.getPage(pageQuery, statement, startDate, endDate);
    }

    @GetMapping("/api/{id}")
    public Result<Statement> getById(@PathVariable Long id) {
        return statementService.getDetail(id);
    }

    @PostMapping("/api/generate")
    public Result<List<Statement>> generate(
            @RequestParam(required = false) Long agentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return statementService.generateByDateRange(agentId, startDate, endDate);
    }

    @PostMapping("/api/generate-single")
    public Result<Statement> generateSingle(
            @RequestParam Long agentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate statDate) {
        return statementService.generateByDate(agentId, statDate);
    }

    @PostMapping("/api/confirm/{id}")
    public Result<Void> confirm(@PathVariable Long id) {
        return statementService.confirmStatement(id);
    }

    @PostMapping("/api/objection/{id}")
    public Result<Void> raiseObjection(@PathVariable Long id, 
                                        @RequestParam(required = false) String remark) {
        return statementService.raiseObjection(id, remark);
    }

    @GetMapping("/api/export")
    public void export(Statement statement,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletResponse response) {
        statementService.exportExcel(statement, startDate, endDate, response);
    }

    @GetMapping("/api/export-detail/{id}")
    public void exportDetail(@PathVariable Long id, HttpServletResponse response) {
        statementService.exportStatementDetail(id, response);
    }
}
