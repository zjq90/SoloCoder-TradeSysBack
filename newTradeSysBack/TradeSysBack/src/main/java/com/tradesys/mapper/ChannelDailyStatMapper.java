package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.ChannelDailyStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface ChannelDailyStatMapper extends BaseMapper<ChannelDailyStat> {

    @Select("SELECT * FROM channel_daily_stat WHERE channel_id = #{channelId} AND stat_date = #{statDate}")
    ChannelDailyStat selectByChannelAndDate(@Param("channelId") Long channelId, @Param("statDate") LocalDate statDate);
}
