package com.dingtalk.h5app.quickstart.mapper;



import com.dingtalk.h5app.quickstart.DTO.UserVotingDTO;

import java.util.List;

public interface UserVotingMapper {
    void insertUserVoting(UserVotingDTO userVotingDTO);

    List<UserVotingDTO> findByUserId(String userId);

    List<UserVotingDTO> findByVid(String vid);


    UserVotingDTO findByUserIdAndVid(String userId, String vid);
}
