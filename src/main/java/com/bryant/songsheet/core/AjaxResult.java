package com.bryant.songsheet.core;

import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpStatus;

/**
 * @author bryant
 * @date 2024/7/8
 **/
public class AjaxResult {
    private int status;
    private String message;
    private Object data;
    private final Long timestamp = System.currentTimeMillis();

    public AjaxResult() {
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public AjaxResult(int status, String msg) {
        this.status = status;
        this.message = msg;
    }

    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    public AjaxResult(int status, String msg, Object data) {
        this.status = status;
        this.message = msg;
        this.data = data;
    }

    public static AjaxResult success() {
        return success("操作成功");
    }

    public static AjaxResult success(Object data) {
        return success("操作成功", data);
    }

    public static AjaxResult success(String msg) {
        return success(msg, (Object)null);
    }

    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(HttpStatus.OK.value(), msg, data);
    }

    public static AjaxResult error() {
        return error("操作失败");
    }

    public static AjaxResult error(String msg) {
        return error(msg, (Object)null);
    }

    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data);
    }

    public static AjaxResult unauthorized(String msg) {
        return new AjaxResult(HttpStatus.UNAUTHORIZED.value(), msg != null ? msg : "尚未登录");
    }

    public Object getData() {
        return this.data;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AjaxResult;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getStatus();
        Object $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }
}