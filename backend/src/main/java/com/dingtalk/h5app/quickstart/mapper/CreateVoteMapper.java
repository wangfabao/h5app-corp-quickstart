package com.dingtalk.h5app.quickstart.mapper;


import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CreateVoteMapper {

    List<CreateVoteDTO> findAllByTitleAndUserid(String title, String userid);

    void insertVote(CreateVoteDTO createVoteDTO);

    CreateVoteDTO findByVid(String uuid32);

    List<CreateVoteDTO> findByStartTime(Date now);

    List<CreateVoteDTO> findByEndTime(Date now);

    List<CreateVoteDTO> findByState(int i);

    void updateState(Integer state, String vid);



    List<CreateVoteDTO> findAllCreateVote();


    CreateVoteDTO findByUserIdAndVid(String vid, String userid);



}
