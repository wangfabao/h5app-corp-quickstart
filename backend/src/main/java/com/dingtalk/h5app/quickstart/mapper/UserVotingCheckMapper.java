package com.dingtalk.h5app.quickstart.mapper;



import com.dingtalk.h5app.quickstart.DTO.UserVotingCheckDTO;

import java.util.List;

public interface UserVotingCheckMapper {
    void insertUserVoting(UserVotingCheckDTO userVotingCheckDTO);

    List<UserVotingCheckDTO> findByUserId(String userId);

    List<UserVotingCheckDTO> findByVid(String vid);


    UserVotingCheckDTO findByUserIdAndVid(String userId, String vid);


    List<String> findByUserVotingCheck(String vid, String userVotingCheck);
}
