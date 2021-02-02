package com.dingtalk.h5app.quickstart.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVotingResult implements Serializable {

    /**
     * 用户投票票数
     */
    private Map DetailsNumber;

//    /**
//     * 当前已投票的人员数
//     */
//    private Integer JoinedVoteNumber;
//
//    /**
//     * 还未参与投票的人员数
//     */
//    private Integer NoJoinedVoteNumber;
}
