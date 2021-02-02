package com.dingtalk.h5app.quickstart.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoteVO {

    /**
     * 投票标题
     */
    private String title;

    /**
     * 投票明细
     */
    private List<String> details;

    /**
     * 是否允许多选
     * true--允许多选,false--不允许投票
     */
    private Boolean checkBox;

    /**
     * 投票开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date startTime;


    /**
     * 投票截止时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date endTime;


    /**
     * 用户userId
     */
    private String userid;
    /**
     * 用户名称
     */
    private String name;

    /**
     * userid_list:接收者的企业内部用户的userid列表，最大用户列表长度100。
     */
    private String userid_list;

    /**
     * dept_id_list:接收者的部门id列表，最大列表长度20。接收者是部门ID下包括子部门下的所有用户。
     */
    private String dept_id_list;
}
