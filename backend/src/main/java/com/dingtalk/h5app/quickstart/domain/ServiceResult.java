package com.dingtalk.h5app.quickstart.domain;

import java.io.Serializable;
import java.util.List;

/**
 * service层返回对象列表封装
 *
 * @author openapi@dingtalk
 * @date 2020/2/4
 */
public class ServiceResult<T> implements Serializable {

    private boolean success = false;

    private String code;

    private String message;

    private T result;

    private ServiceResult() {
    }

    public static <T> ServiceResult<T> success(T result) {
        ServiceResult<T> item = new ServiceResult<T>();
        item.success = true;
        item.result = result;
        item.code = "0";
        item.message = "success";
        return item;
    }
//    public static <T> ServiceResult<T> success(long count, List<T> data) {
//        ServiceResult<T> item = new ServiceResult<T>();
//        item.success = true;
//        item.count = count;
//        item.data = data;
//        item.code = "0";
//        item.message = "success";
//        return item;
//    }


    public static <T> ServiceResult<T> failure(String errorCode, String errorMessage) {
        ServiceResult<T> item = new ServiceResult<T>();
        item.success = false;
        item.code = errorCode;
        item.message = errorMessage;
        return item;
    }

    public static <T> ServiceResult<T> failure(String errorCode) {
        ServiceResult<T> item = new ServiceResult<T>();
        item.success = false;
        item.code = errorCode;
        item.message = "failure";
        return item;
    }

    public boolean hasResult() {
        return result != null;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getResult() {
        return result;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
