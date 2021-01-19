package com.dingtalk.h5app.quickstart.bean;

/**
 * @author: 王法宝
 * @DATE: 2021/1/10 17:09
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 前端页面用到的所有ajax返回结果统一封装
 */
@Data
@AllArgsConstructor
public class AjaxMessage {
    private boolean status;
    private String message;
    private List<String> listMassgage;
    private Object result;

    public AjaxMessage(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public AjaxMessage(boolean status, List<String> listMassgage) {
        this.status = status;
        this.listMassgage = listMassgage;
    }

    public AjaxMessage(boolean status, Object result) {
        this.status = status;
        this.result = result;
    }
}
