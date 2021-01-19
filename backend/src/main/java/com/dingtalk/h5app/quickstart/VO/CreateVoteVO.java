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
     * 投票截止时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endtime;

    /**
     * 发起投票人
     */
    private String userId;
}
