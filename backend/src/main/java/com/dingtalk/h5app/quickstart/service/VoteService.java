package com.dingtalk.h5app.quickstart.service;


import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import com.dingtalk.h5app.quickstart.VO.CreateVoteVO;
import com.dingtalk.h5app.quickstart.VO.UserVotingVO;
import com.dingtalk.h5app.quickstart.VO.ViewVoteVO;
import com.dingtalk.h5app.quickstart.VO.VotingResultStatisticsVO;
import com.dingtalk.h5app.quickstart.pojo.UserVoting;

public interface VoteService {
    CreateVoteDTO insertVote(CreateVoteVO createVoteVO);

    UserVoting userVoting(UserVotingVO userVotingVO);

    ViewVoteVO viewVote(String userId, String vid);

    VotingResultStatisticsVO votingResultStatistics(String userId, String vid);
}
