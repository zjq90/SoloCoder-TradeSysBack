package com.tradesys.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Machine;
import com.tradesys.entity.Merchant;
import com.tradesys.entity.Product;
import com.tradesys.mapper.MachineMapper;
import com.tradesys.mapper.MerchantMapper;
import com.tradesys.mapper.ProductMapper;
import com.tradesys.service.MachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl extends ServiceImpl<MachineMapper, Machine> implements MachineService {

    private final ProductMapper productMapper;
    private final MerchantMapper merchantMapper;

    @Override
    public Result<List<Machine>> getPage(PageQuery pageQuery, Machine machine) {
        Page<Machine> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<Machine> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(machine.getMachineNo())) {
            wrapper.like(Machine::getMachineNo, machine.getMachineNo());
        }
        if (StrUtil.isNotBlank(machine.getSn())) {
            wrapper.like(Machine::getSn, machine.getSn());
        }
        if (machine.getProductId() != null) {
            wrapper.eq(Machine::getProductId, machine.getProductId());
        }
        if (machine.getAgentId() != null) {
            wrapper.eq(Machine::getAgentId, machine.getAgentId());
        }
        if (machine.getMerchantId() != null) {
            wrapper.eq(Machine::getMerchantId, machine.getMerchantId());
        }
        if (machine.getStatus() != null) {
            wrapper.eq(Machine::getStatus, machine.getStatus());
        }
        wrapper.orderByDesc(Machine::getCreateTime);

        Page<Machine> resultPage = this.page(page, wrapper);

        // 补充关联信息
        for (Machine m : resultPage.getRecords()) {
            if (m.getProductId() != null) {
                Product product = productMapper.selectById(m.getProductId());
                if (product != null) {
                    m.setProductName(product.getProductName());
                }
            }
            if (m.getMerchantId() != null) {
                Merchant merchant = merchantMapper.selectById(m.getMerchantId());
                if (merchant != null) {
                    m.setMerchantName(merchant.getMerchantName());
                }
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<Machine> getDetail(Long id) {
        Machine machine = this.getById(id);
        if (machine == null) {
            return Result.error("机器不存在");
        }

        if (machine.getProductId() != null) {
            Product product = productMapper.selectById(machine.getProductId());
            if (product != null) {
                machine.setProductName(product.getProductName());
            }
        }
        if (machine.getMerchantId() != null) {
            Merchant merchant = merchantMapper.selectById(machine.getMerchantId());
            if (merchant != null) {
                machine.setMerchantName(merchant.getMerchantName());
            }
        }

        return Result.success(machine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addMachine(Machine machine) {
        // 检查机器编号是否存在
        if (StrUtil.isNotBlank(machine.getMachineNo())) {
            Machine exist = baseMapper.selectByMachineNo(machine.getMachineNo());
            if (exist != null) {
                return Result.error("机器编号已存在");
            }
        } else {
            machine.setMachineNo(generateMachineNo());
        }

        // 检查SN码是否存在
        if (StrUtil.isNotBlank(machine.getSn())) {
            Machine exist = baseMapper.selectBySn(machine.getSn());
            if (exist != null) {
                return Result.error("SN码已存在");
            }
        }

        machine.setStatus(0);
        this.save(machine);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateMachine(Machine machine) {
        Machine existMachine = this.getById(machine.getId());
        if (existMachine == null) {
            return Result.error("机器不存在");
        }

        this.updateById(machine);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteMachine(Long id) {
        Machine machine = this.getById(id);
        if (machine == null) {
            return Result.error("机器不存在");
        }

        if (machine.getMerchantId() != null) {
            return Result.error("机器已绑定商户，无法删除");
        }

        this.removeById(id);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> bindMerchant(Long machineId, Long merchantId) {
        Machine machine = this.getById(machineId);
        if (machine == null) {
            return Result.error("机器不存在");
        }

        if (machine.getMerchantId() != null) {
            return Result.error("机器已绑定商户");
        }

        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            return Result.error("商户不存在");
        }

        machine.setMerchantId(merchantId);
        machine.setStatus(2);
        machine.setActivateTime(LocalDateTime.now());
        this.updateById(machine);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> unbindMerchant(Long machineId) {
        Machine machine = this.getById(machineId);
        if (machine == null) {
            return Result.error("机器不存在");
        }

        machine.setMerchantId(null);
        machine.setStatus(1);
        this.updateById(machine);

        return Result.ok();
    }

    @Override
    public void exportTemplate(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("机器导入模板");

            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 创建提示样式
            CellStyle noteStyle = workbook.createCellStyle();
            noteStyle.setWrapText(true);
            Font noteFont = workbook.createFont();
            noteFont.setColor(IndexedColors.RED.getIndex());
            noteStyle.setFont(noteFont);

            // 表头
            String[] headers = {"机器编号*", "SN码*", "MAC地址", "产品编号*"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            // 示例数据行
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("MAC2024000001");
            exampleRow.createCell(1).setCellValue("SN2024000001");
            exampleRow.createCell(2).setCellValue("00:11:22:33:44:AA");
            exampleRow.createCell(3).setCellValue("PRD001");

            // 提示说明
            Row noteRow1 = sheet.createRow(3);
            Cell noteCell1 = noteRow1.createCell(0);
            noteCell1.setCellValue("说明：");
            noteCell1.setCellStyle(noteStyle);

            Row noteRow2 = sheet.createRow(4);
            noteRow2.createCell(0).setCellValue("1. 带*号的为必填项");

            Row noteRow3 = sheet.createRow(5);
            noteRow3.createCell(0).setCellValue("2. 产品编号请从系统中获取（如：PRD001）");

            Row noteRow4 = sheet.createRow(6);
            noteRow4.createCell(0).setCellValue("3. 机器编号和SN码不能重复");

            // 设置响应
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("机器导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出模板失败", e);
            throw new RuntimeException("导出模板失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> importMachines(MultipartFile file, Long agentId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> successList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();

        // 先获取所有产品，用于校验产品编号
        List<Product> products = productMapper.selectList(null);
        Map<String, Product> productMap = new HashMap<>();
        for (Product p : products) {
            productMap.put(p.getProductNo(), p);
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 从第2行开始读取数据（跳过表头和示例）
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Map<String, Object> rowData = new HashMap<>();
                rowData.put("rowNum", i + 1);

                try {
                    // 机器编号
                    Cell machineNoCell = row.getCell(0);
                    String machineNo = getCellValueAsString(machineNoCell);
                    if (StrUtil.isBlank(machineNo)) {
                        rowData.put("error", "机器编号不能为空");
                        errorList.add(rowData);
                        continue;
                    }

                    // 检查机器编号是否已存在
                    Machine existMachine = baseMapper.selectByMachineNo(machineNo);
                    if (existMachine != null) {
                        rowData.put("error", "机器编号已存在");
                        errorList.add(rowData);
                        continue;
                    }

                    // SN码
                    Cell snCell = row.getCell(1);
                    String sn = getCellValueAsString(snCell);
                    if (StrUtil.isBlank(sn)) {
                        rowData.put("error", "SN码不能为空");
                        errorList.add(rowData);
                        continue;
                    }

                    // 检查SN码是否已存在
                    Machine existSn = baseMapper.selectBySn(sn);
                    if (existSn != null) {
                        rowData.put("error", "SN码已存在");
                        errorList.add(rowData);
                        continue;
                    }

                    // MAC地址
                    Cell macCell = row.getCell(2);
                    String macAddress = getCellValueAsString(macCell);

                    // 产品编号
                    Cell productNoCell = row.getCell(3);
                    String productNo = getCellValueAsString(productNoCell);
                    if (StrUtil.isBlank(productNo)) {
                        rowData.put("error", "产品编号不能为空");
                        errorList.add(rowData);
                        continue;
                    }

                    Product product = productMap.get(productNo);
                    if (product == null) {
                        rowData.put("error", "产品编号不存在：" + productNo);
                        errorList.add(rowData);
                        continue;
                    }

                    // 创建机器
                    Machine machine = new Machine();
                    machine.setMachineNo(machineNo);
                    machine.setSn(sn);
                    machine.setMacAddress(macAddress);
                    machine.setProductId(product.getId());
                    machine.setAgentId(agentId);
                    machine.setStatus(agentId != null ? 1 : 0);
                    machine.setPurchaseDate(LocalDate.now());

                    this.save(machine);

                    rowData.put("machineNo", machineNo);
                    rowData.put("sn", sn);
                    rowData.put("productName", product.getProductName());
                    successList.add(rowData);

                } catch (Exception e) {
                    rowData.put("error", "数据解析异常：" + e.getMessage());
                    errorList.add(rowData);
                }
            }

        } catch (IOException e) {
            log.error("导入机器数据失败", e);
            return Result.error("导入失败：" + e.getMessage());
        }

        result.put("totalCount", successList.size() + errorList.size());
        result.put("successCount", successList.size());
        result.put("errorCount", errorList.size());
        result.put("successList", successList);
        result.put("errorList", errorList);

        return Result.success(result);
    }

    @Override
    public Result<List<Map<String, Object>>> getMachineOptions() {
        List<Machine> machines = this.list(new LambdaQueryWrapper<Machine>()
                .isNull(Machine::getMerchantId)
                .orderByDesc(Machine::getCreateTime));

        List<Map<String, Object>> options = new ArrayList<>();
        for (Machine machine : machines) {
            Map<String, Object> option = new HashMap<>();
            option.put("id", machine.getId());
            option.put("machineNo", machine.getMachineNo());
            option.put("sn", machine.getSn());
            option.put("productId", machine.getProductId());
            options.add(option);
        }

        return Result.success(options);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private String generateMachineNo() {
        return "MAC" + IdUtil.getSnowflake(1, 1).nextIdStr();
    }
}
