package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Channel;
import com.tradesys.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping("/api/list")
    public Result<List<Channel>> list(PageQuery pageQuery, Channel channel) {
        return channelService.getPage(pageQuery, channel);
    }

    @GetMapping("/api/{id}")
    public Result<Channel> getById(@PathVariable Long id) {
        return channelService.getDetail(id);
    }

    @GetMapping("/api/options")
    public Result<List<Map<String, Object>>> options() {
        return channelService.getChannelOptions();
    }

    @PostMapping("/api/add")
    public Result<Void> add(@RequestBody Channel channel) {
        return channelService.addChannel(channel);
    }

    @PostMapping("/api/update")
    public Result<Void> update(@RequestBody Channel channel) {
        return channelService.updateChannel(channel);
    }

    @PostMapping("/api/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return channelService.deleteChannel(id);
    }

    @PostMapping("/api/update-status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        return channelService.updateStatus(id, status);
    }

    @PostMapping("/api/update-threshold")
    public Result<Void> updateThreshold(@RequestParam Long id, @RequestParam BigDecimal thresholdPercent) {
        return channelService.updateThreshold(id, thresholdPercent);
    }

    @PostMapping("/api/update-daily-limit")
    public Result<Void> updateDailyLimit(@RequestParam Long id, @RequestParam BigDecimal dailyLimit) {
        return channelService.updateDailyLimit(id, dailyLimit);
    }

    @GetMapping("/api/export")
    public void export(Channel channel, HttpServletResponse response) {
        channelService.exportExcel(channel, response);
    }
}
