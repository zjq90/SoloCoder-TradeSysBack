package com.tradesys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Machine;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface MachineService extends IService<Machine> {

    Result<List<Machine>> getPage(PageQuery pageQuery, Machine machine);

    Result<Machine> getDetail(Long id);

    Result<Void> addMachine(Machine machine);

    Result<Void> updateMachine(Machine machine);

    Result<Void> deleteMachine(Long id);

    Result<Void> bindMerchant(Long machineId, Long merchantId);

    Result<Void> unbindMerchant(Long machineId);

    void exportTemplate(HttpServletResponse response);

    Result<Map<String, Object>> importMachines(MultipartFile file, Long agentId);

    Result<List<Map<String, Object>>> getMachineOptions();
}
