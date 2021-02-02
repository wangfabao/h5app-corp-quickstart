package com.dingtalk.h5app.quickstart.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: 王法宝
 * @DATE: 2021/1/10 17:11
 */
@Data
@AllArgsConstructor
public class TableData<T> {
    private final boolean success;
    private String code;
    private String message;
    private long count;
    private List<T> result;

    public TableData(long count, List<T> result) {
        this.success = true;
        this.code = "0";
        this.message = "success";
        this.count = count;
        this.result = result;
    }
}
