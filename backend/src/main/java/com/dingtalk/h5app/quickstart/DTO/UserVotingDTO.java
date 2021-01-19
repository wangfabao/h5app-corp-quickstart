package com.dingtalk.h5app.quickstart.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVotingDTO {

    private Integer id;
    /**
     * 投票的唯一标识
     */
    private String vid;

    private String userVotingChecks;

    /**
     * 投票参与者
     */
    private String userId;
}
