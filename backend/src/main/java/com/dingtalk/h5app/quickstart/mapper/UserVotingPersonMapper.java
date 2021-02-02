package com.dingtalk.h5app.quickstart.mapper;

import com.dingtalk.h5app.quickstart.DTO.UserVotingPersonDTO;
import com.dingtalk.h5app.quickstart.DTO.UserVotingPersonExcelDTO;
import com.dingtalk.h5app.quickstart.pojo.JoinedVoteNumber;

import java.util.ArrayList;
import java.util.List;

public interface UserVotingPersonMapper {
    void insertUserVoting(UserVotingPersonDTO userVotingPersonDTO);

    List<String> findByVid(String vid);

     List<JoinedVoteNumber> findByVid2(String vid);

    UserVotingPersonDTO findByVidAndUserId(String vid, String userid);

    List<UserVotingPersonExcelDTO> findVotingResultByVid(String vid);
}
