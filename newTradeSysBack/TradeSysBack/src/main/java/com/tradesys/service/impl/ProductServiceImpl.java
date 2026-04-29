package com.tradesys.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Machine;
import com.tradesys.entity.Product;
import com.tradesys.mapper.MachineMapper;
import com.tradesys.mapper.ProductMapper;
import com.tradesys.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final MachineMapper machineMapper;

    @Override
    public Result<List<Product>> getPage(PageQuery pageQuery, Product product) {
        Page<Product> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(product.getProductName())) {
            wrapper.like(Product::getProductName, product.getProductName());
        }
        if (StrUtil.isNotBlank(product.getProductNo())) {
            wrapper.eq(Product::getProductNo, product.getProductNo());
        }
        if (product.getStatus() != null) {
            wrapper.eq(Product::getStatus, product.getStatus());
        }
        if (product.getProductType() != null) {
            wrapper.eq(Product::getProductType, product.getProductType());
        }
        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> resultPage = this.page(page, wrapper);

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<Product> getDetail(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            return Result.error("产品不存在");
        }
        return Result.success(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addProduct(Product product) {
        if (StrUtil.isBlank(product.getProductNo())) {
            product.setProductNo(generateProductNo());
        } else {
            Product exist = baseMapper.selectOne(
                new LambdaQueryWrapper<Product>().eq(Product::getProductNo, product.getProductNo())
            );
            if (exist != null) {
                return Result.error("产品编号已存在");
            }
        }

        if (product.getStatus() == null) {
            product.setStatus(1);
        }

        this.save(product);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateProduct(Product product) {
        Product existProduct = this.getById(product.getId());
        if (existProduct == null) {
            return Result.error("产品不存在");
        }

        this.updateById(product);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteProduct(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            return Result.error("产品不存在");
        }

        Long machineCount = machineMapper.selectCount(
            new LambdaQueryWrapper<Machine>().eq(Machine::getProductId, id)
        );
        if (machineCount > 0) {
            return Result.error("产品下存在机器，无法删除");
        }

        this.removeById(id);

        return Result.ok();
    }

    @Override
    public Result<List<Map<String, Object>>> getProductOptions() {
        List<Product> products = this.list(
            new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByAsc(Product::getProductNo)
        );

        List<Map<String, Object>> options = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> option = new HashMap<>();
            option.put("id", product.getId());
            option.put("productNo", product.getProductNo());
            option.put("productName", product.getProductName());
            option.put("defaultRate", product.getDefaultRate());
            options.add(option);
        }

        return Result.success(options);
    }

    @Override
    public void exportExcel(Product product, HttpServletResponse response) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(product.getProductName())) {
            wrapper.like(Product::getProductName, product.getProductName());
        }
        if (StrUtil.isNotBlank(product.getProductNo())) {
            wrapper.eq(Product::getProductNo, product.getProductNo());
        }
        if (product.getStatus() != null) {
            wrapper.eq(Product::getStatus, product.getStatus());
        }
        if (product.getProductType() != null) {
            wrapper.eq(Product::getProductType, product.getProductType());
        }
        wrapper.orderByDesc(Product::getCreateTime);

        List<Product> list = this.list(wrapper);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("产品信息");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat moneyFormat = workbook.createDataFormat();
            moneyStyle.setDataFormat(moneyFormat.getFormat("#,##0.00"));

            String[] headers = {"产品编号", "产品名称", "产品类型", "单价", "成本价", "默认费率", "状态", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }

            int rowNum = 1;
            for (Product p : list) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(p.getProductNo() != null ? p.getProductNo() : "");
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(p.getProductName() != null ? p.getProductName() : "");

                Cell cell2 = row.createCell(2);
                String typeText = "";
                if (p.getProductType() != null) {
                    switch (p.getProductType()) {
                        case 1: typeText = "POS机"; break;
                        case 2: typeText = "扫码枪"; break;
                        case 3: typeText = "其他"; break;
                        default: typeText = "未知";
                    }
                }
                cell2.setCellValue(typeText);
                cell2.setCellStyle(centerStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(p.getUnitPrice() != null ? p.getUnitPrice().doubleValue() : 0);
                cell3.setCellStyle(moneyStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(p.getCostPrice() != null ? p.getCostPrice().doubleValue() : 0);
                cell4.setCellStyle(moneyStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(p.getDefaultRate() != null ? p.getDefaultRate().toString() : "");
                cell5.setCellStyle(centerStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(p.getStatus() != null && p.getStatus() == 1 ? "启用" : "禁用");
                cell6.setCellStyle(centerStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(p.getCreateTime() != null ? p.getCreateTime().toString().replace("T", " ") : "");
                cell7.setCellStyle(centerStyle);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("产品信息列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出产品Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    private String generateProductNo() {
        return "PRD" + IdUtil.getSnowflake(1, 1).nextIdStr();
    }
}
