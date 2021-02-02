package com.dingtalk.h5app.quickstart.DTO;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserVotingPersonExcelDTO {

    /**
     * 主键id
     */
    @ExcelProperty("主键id")
    private Integer id;

    /**
     * 投票的唯一标识
     */
    @ExcelProperty("投票的唯一标识")
    private String vid;

    /**
     * 用户投票选项
     */
    @ExcelProperty("用户投票选项")
    private String userVotingChecks;

    /**
     * 用户userId
     */
    @ExcelProperty("用户userId")
    private String userid;

    /**
     * 用户名称
     */
    @ExcelProperty("用户名称")
    private String name;

    /**
     * 用户开始投票时间
     */
    @ExcelProperty("用户开始投票时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date userVotingStartTime;
}
