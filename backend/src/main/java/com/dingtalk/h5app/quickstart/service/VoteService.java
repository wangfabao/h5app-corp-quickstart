package com.dingtalk.h5app.quickstart.service;


import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import com.dingtalk.h5app.quickstart.VO.*;
import com.dingtalk.h5app.quickstart.domain.ServiceResult;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResult;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResultStatistics1;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResultStatistics2;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface VoteService {

    PageInfo<CreatedVoteVO> findByTitleAndUserid(String title, String userid, Integer page, Integer limit);

    List<CreateVoteDTO> findAllByTitleAndUserid(String title,String userid);

    String insertVote(CreateVoteVO createVoteVO);

    CreateVoteDTO findByVid(String uuid32);

    ServiceResult userVoting(UserVotingVO userVotingVO);

    UserVotingResult viewVote(String vid);

    UserVotingResultStatistics1 votingResultStatistics(String vid, String userid);

    UserVotingResultStatistics2 votingResultStatistics2(String vid, String userid);

    ServiceResult downloadVotingResult(String vid, String userid);




//    ViewVoteVO viewVote(String userId, String vid);
//
//    VotingResultStatisticsVO votingResultStatistics(String userId, String vid);


}
