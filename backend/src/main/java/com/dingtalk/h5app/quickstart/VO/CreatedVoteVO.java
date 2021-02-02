package com.dingtalk.h5app.quickstart.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedVoteVO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 投票的唯一标识
     */
    private String vid;

    /**
     * 投票标题
     */
    private String title;

    /**
     * 投票明细
     */
    private String details;

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
     * 创建投票时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date createTime;

    /**
     * 用户userId
     */
    private String userid;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 投票状态
     *  0--未开始
     *  1--进行中
     *  2--已结束
     */
    private Integer state;
}
