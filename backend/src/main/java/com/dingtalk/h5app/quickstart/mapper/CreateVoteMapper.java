package com.dingtalk.h5app.quickstart.mapper;


import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;

public interface CreateVoteMapper {

    void insertVote(CreateVoteDTO createVoteDTO);

    CreateVoteDTO findByVid(String uuid32);

    CreateVoteDTO findByUserIdAndVid(String userId,String vid);
}
