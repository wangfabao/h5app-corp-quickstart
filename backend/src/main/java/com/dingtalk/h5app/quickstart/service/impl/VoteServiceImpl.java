package com.dingtalk.h5app.quickstart.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import com.dingtalk.h5app.quickstart.DTO.UserVotingDTO;
import com.dingtalk.h5app.quickstart.VO.CreateVoteVO;
import com.dingtalk.h5app.quickstart.VO.UserVotingVO;
import com.dingtalk.h5app.quickstart.VO.ViewVoteVO;
import com.dingtalk.h5app.quickstart.VO.VotingResultStatisticsVO;
import com.dingtalk.h5app.quickstart.config.UuidUtil;
import com.dingtalk.h5app.quickstart.mapper.CreateVoteMapper;
import com.dingtalk.h5app.quickstart.mapper.UserVotingMapper;
import com.dingtalk.h5app.quickstart.pojo.UserVoting;
import com.dingtalk.h5app.quickstart.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    CreateVoteMapper createVoteMapper;

    @Autowired
    UserVotingMapper userVotingMapper;

//    @Autowired
//    DingTalkUtils dingTalkUtils;

    @Override
    public CreateVoteDTO insertVote(CreateVoteVO createVoteVO) {
        CreateVoteDTO createVoteDTO = new CreateVoteDTO();
        String uuid32 = UuidUtil.getUUID32();
        createVoteDTO.setVid(uuid32);
        createVoteDTO.setTitle(createVoteVO.getTitle());
        createVoteDTO.setDetails(JSONUtils.toJSONString(createVoteVO.getDetails()));
        createVoteDTO.setCheckBox(createVoteVO.getCheckBox());
        createVoteDTO.setEndtime(createVoteVO.getEndtime());
        createVoteDTO.setUserId(createVoteVO.getUserId());
//        将前台传过来的参数存入数据库
        createVoteMapper.insertVote(createVoteDTO);
//        根据vid(投票标识)查出是哪一个投票
        CreateVoteDTO voteDTO = createVoteMapper.findByVid(uuid32);
//        /**
//         * 发送投票消息
//         */
//        dingTalkUtils.sendVoteMsg();
        return voteDTO;

    }


    @Override
    public UserVoting userVoting(UserVotingVO userVotingVO) {
        CreateVoteDTO createVoteContent = createVoteMapper.findByVid(userVotingVO.getVid());

//      判断投票人是否已经投票
        UserVotingDTO byUserIdAndVid = userVotingMapper.findByUserIdAndVid(userVotingVO.getUserId(), userVotingVO.getVid());
        if (byUserIdAndVid != null){
            System.out.println("请勿再次投票");
            return null;
        }
//        判断投票是否截止
        long endtime = createVoteContent.getEndtime().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date now = new Date();
        try {
            long starttime = sdf.parse(sdf.format(now)).getTime();
            if (starttime > endtime){
                System.out.println("截至投票");
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        如果是多选的情况下
//        if (createVoteContent.getCheckBox().booleanValue()){
            UserVotingDTO userVotingDTO = new UserVotingDTO();
            userVotingDTO.setVid(userVotingVO.getVid());
            userVotingDTO.setUserId(userVotingVO.getUserId());
//            List<String> userVotingChecks = userVotingDTO.getUserVotingChecks();
            ArrayList<String> list = new ArrayList<>();
            for (String userVotingCheck : userVotingVO.getUserVotingChecks()) {
                list.add(userVotingCheck);
            }
            userVotingDTO.setUserVotingChecks(JSONUtils.toJSONString(list));
            userVotingMapper.insertUserVoting(userVotingDTO);
//        }
//        else {
////            如果时单选的情况下
//            UserVotingDTO userVotingDTO = new UserVotingDTO();
//            userVotingDTO.setVid(userVotingVO.getVid());
//            userVotingDTO.setUserId(userVotingVO.getUserId());
//            userVotingDTO.setUserVotingChecks(userVotingVO.getUserVotingChecks().toString());
//            userVotingMapper.insertUserVoting(userVotingDTO);
//        }

//        用户投票结束后查询结果
        UserVoting userVoting = new UserVoting();
        ArrayList<Object> list1 = new ArrayList<>();
        ArrayList<Object> list2 = new ArrayList<>();
        List<UserVotingDTO> userVotingDTOS = userVotingMapper.findByVid(userVotingVO.getVid());
        for (UserVotingDTO userVotingDTO1 : userVotingDTOS) {
            list1.add(userVotingDTO1.getUserId());
            JSONArray array = JSONObject.parseArray(userVotingDTO1.getUserVotingChecks());
            for (int i = 0; i < array.size(); i++) {
                list2.add(array.get(i));
            }

        }
        userVoting.setParticipated(list1);
        userVoting.setVotingResult(list2);
        return userVoting;
    }


    @Override
    public ViewVoteVO viewVote(String userId, String vid) {
        ViewVoteVO viewVoteVO = new ViewVoteVO();
        CreateVoteDTO createVoteDTO = createVoteMapper.findByVid(vid);
        viewVoteVO.setTitle(createVoteDTO.getTitle());
        viewVoteVO.setCheckBox(createVoteDTO.getCheckBox());
        viewVoteVO.setEndtime(createVoteDTO.getEndtime());
        UserVotingDTO userVotingDTO = userVotingMapper.findByUserIdAndVid(userId,vid);
        if (userVotingDTO == null){
            System.out.println("用户还未投票");
            return null;
        }
        JSONArray array = JSONObject.parseArray(userVotingDTO.getUserVotingChecks());
        viewVoteVO.setVoteResult(array);
        return viewVoteVO;
    }

    @Override
    public VotingResultStatisticsVO votingResultStatistics(String userId, String vid) {
//        判断发起者是否已经发起投票
        CreateVoteDTO createVoteDTO = createVoteMapper.findByUserIdAndVid(userId,vid);
        if (createVoteDTO == null) {
            System.out.println("还未发起投票");
            return null;
        }

//        发起者查看投票结果和投票内容
        List<UserVotingDTO> userVotingDTOList = userVotingMapper.findByVid(vid);
        VotingResultStatisticsVO votingResultStatisticsVO = new VotingResultStatisticsVO();
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<Object> list2 = new ArrayList<>();
        for (UserVotingDTO userVotingDTO : userVotingDTOList) {
            list1.add(userVotingDTO.getUserId());
            votingResultStatisticsVO.setParticipated(list1);
            JSONArray array = JSONObject.parseArray(userVotingDTO.getUserVotingChecks());
            for (Object o : array) {
                list2.add(o);
            }
        }
        votingResultStatisticsVO.setVotingResult(list2);
        return votingResultStatisticsVO;
    }

}
