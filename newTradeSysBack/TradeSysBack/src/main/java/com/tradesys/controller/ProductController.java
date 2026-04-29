package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Product;
import com.tradesys.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/list")
    public Result<List<Product>> list(PageQuery pageQuery, Product product) {
        return productService.getPage(pageQuery, product);
    }

    @GetMapping("/api/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        return productService.getDetail(id);
    }

    @GetMapping("/api/options")
    public Result<List<Map<String, Object>>> options() {
        return productService.getProductOptions();
    }

    @PostMapping("/api/add")
    public Result<Void> add(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PostMapping("/api/update")
    public Result<Void> update(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @PostMapping("/api/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/api/export")
    public void export(Product product, HttpServletResponse response) {
        productService.exportExcel(product, response);
    }
}
