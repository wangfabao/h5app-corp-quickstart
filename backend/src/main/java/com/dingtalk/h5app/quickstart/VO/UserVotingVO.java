package com.dingtalk.h5app.quickstart.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVotingVO {
    /**
     * 投票的唯一标识
     */
    private String vid;

//    /**
//     * 是否允许多选
//     * true--允许多选,false--不允许投票
//     */
//    private Boolean checkBox;
//
    /**
     * 投票明细
     */
    private List<String> userVotingChecks;
//
//    /**
//     * 投票截止时间
//     */
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
//    private Date endtime;

    /**
     * 用户userId
     */
    private String userid;
    /**
     * 用户名称
     */
    private String name;

}
