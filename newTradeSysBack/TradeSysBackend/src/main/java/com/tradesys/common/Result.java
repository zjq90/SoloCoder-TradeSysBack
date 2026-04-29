package com.tradesys.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一响应结果类
 * 用于封装所有API接口的返回结果
 *
 * @param <T> 数据类型
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     * 200: 成功
     * 400: 请求参数错误
     * 401: 未授权
     * 403: 禁止访问
     * 500: 服务器内部错误
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 总记录数（用于分页查询）
     */
    private Long total;

    /**
     * 默认构造函数
     */
    public Result() {
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     */
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    /**
     * 成功响应（无数据）
     *
     * @return Result<Void>
     */
    public static Result<Void> ok() {
        return new Result<>(200, "操作成功");
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(200, "操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带数据和总记录数，用于分页）
     *
     * @param data  数据列表
     * @param total 总记录数
     * @param <T>   数据类型
     * @return Result<List<T>>
     */
    public static <T> Result<List<T>> success(List<T> data, Long total) {
        Result<List<T>> result = new Result<>(200, "操作成功");
        result.setData(data);
        result.setTotal(total);
        return result;
    }

    /**
     * 成功响应（带自定义消息和数据）
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>(200, message);
        result.setData(data);
        return result;
    }

    // ==================== 失败响应 ====================

    /**
     * 失败响应（默认消息）
     *
     * @param <T> 数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }

    /**
     * 失败响应（自定义消息）
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    /**
     * 失败响应（自定义状态码和消息）
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    // ==================== 工具方法 ====================

    /**
     * 判断是否成功
     *
     * @return true 成功, false 失败
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
