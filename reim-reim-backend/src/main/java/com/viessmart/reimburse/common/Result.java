package com.viessmart.reimburse.common;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int code;
    private String message;
    private T data;
    public Result() {
    }
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    public static <T> Result<T> error(T data) {
        return new Result<>(500, "error", data);
    }
    public static <T> Result<T> error(String message, T data) {
        return new Result<>(500, message, data);
    }
    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
    /**
     * 通用分页成功响应
     * @param page MyBatis-Plus 分页对象
     * @param <T>  泛型，代表分页中的实体类型
     * @return 统一响应结果
     */
    public static <T> Result<Page<T>> page(Page<T> page) {
        Result<Page<T>> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(page);
        return result;
    }


    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
