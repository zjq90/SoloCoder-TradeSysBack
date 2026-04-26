package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Channel;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ChannelService extends IService<Channel> {

    Result<List<Channel>> getPage(PageQuery pageQuery, Channel channel);

    Result<Channel> getDetail(Long id);

    Result<Void> addChannel(Channel channel);

    Result<Void> updateChannel(Channel channel);

    Result<Void> deleteChannel(Long id);

    Result<Void> updateStatus(Long id, Integer status);

    Result<Void> updateThreshold(Long id, BigDecimal thresholdPercent);

    Result<Void> updateDailyLimit(Long id, BigDecimal dailyLimit);

    Result<List<Map<String, Object>>> getChannelOptions();

    void exportExcel(Channel channel, HttpServletResponse response);
}
