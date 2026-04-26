package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Product;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ProductService extends IService<Product> {

    Result<List<Product>> getPage(PageQuery pageQuery, Product product);

    Result<Product> getDetail(Long id);

    Result<Void> addProduct(Product product);

    Result<Void> updateProduct(Product product);

    Result<Void> deleteProduct(Long id);

    Result<List<Map<String, Object>>> getProductOptions();

    void exportExcel(Product product, HttpServletResponse response);
}
