package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 产品Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 查询所有启用的产品
     *
     * @return 产品列表
     */
    @Select("SELECT * FROM product WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<Product> selectAllActive();

    /**
     * 根据产品类型查询产品列表
     *
     * @param productType 产品类型
     * @return 产品列表
     */
    @Select("SELECT * FROM product WHERE product_type = #{productType} AND status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<Product> selectByType(@Param("productType") Integer productType);

    /**
     * 统计产品数量
     *
     * @param status 状态（可选）
     * @return 产品数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM product WHERE deleted = 0 " +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</script>")
    long countByStatus(@Param("status") Integer status);
}
