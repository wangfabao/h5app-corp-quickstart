package com.dingtalk.h5app.quickstart.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVotingCheckDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 投票的唯一标识
     */
    private String vid;

    /**
     * 用户投票选项
     */
    private String userVotingCheck;

    /**
     * 用户开始投票时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date userVotingStartTime;
}
