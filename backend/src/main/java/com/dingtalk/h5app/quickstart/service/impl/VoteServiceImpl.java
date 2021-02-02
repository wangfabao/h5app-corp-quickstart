package com.dingtalk.h5app.quickstart.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.excel.EasyExcel;
import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import com.dingtalk.h5app.quickstart.DTO.UserVotingCheckDTO;
import com.dingtalk.h5app.quickstart.DTO.UserVotingPersonDTO;
import com.dingtalk.h5app.quickstart.DTO.UserVotingPersonExcelDTO;
import com.dingtalk.h5app.quickstart.VO.*;
import com.dingtalk.h5app.quickstart.config.DingTalkUtils;
import com.dingtalk.h5app.quickstart.config.UuidUtil;
import com.dingtalk.h5app.quickstart.domain.ServiceResult;
import com.dingtalk.h5app.quickstart.mapper.CreateVoteMapper;
import com.dingtalk.h5app.quickstart.mapper.UserVotingCheckMapper;
import com.dingtalk.h5app.quickstart.mapper.UserVotingPersonMapper;
import com.dingtalk.h5app.quickstart.pojo.JoinedVoteNumber;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResult;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResultStatistics1;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResultStatistics2;
import com.dingtalk.h5app.quickstart.service.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    CreateVoteMapper createVoteMapper;

    @Autowired
    UserVotingCheckMapper userVotingCheckMapper;

    @Autowired
    UserVotingPersonMapper userVotingPersonMapper;

    @Autowired
    DingTalkUtils dingTalkUtils;

    @Override
    public PageInfo<CreatedVoteVO> findByTitleAndUserid(String title, String userid, Integer page, Integer limit) {
        PageHelper.startPage(page,limit);
        List<CreateVoteDTO> createVoteDTOList = createVoteMapper.findAllByTitleAndUserid(title,userid);
        List<CreatedVoteVO> createdVoteVOList = new ArrayList<>();
        for (CreateVoteDTO createVoteDTO : createVoteDTOList) {
            CreatedVoteVO createdVoteVO = new CreatedVoteVO();
            createdVoteVO.setId(createVoteDTO.getId());
            createdVoteVO.setVid(createVoteDTO.getVid());
            createdVoteVO.setTitle(createVoteDTO.getTitle());
            createdVoteVO.setDetails(createVoteDTO.getDetails());
            createdVoteVO.setCheckBox(createVoteDTO.getCheckBox());
            createdVoteVO.setStartTime(createVoteDTO.getStartTime());
            createdVoteVO.setEndTime(createVoteDTO.getEndTime());
            createdVoteVO.setCreateTime(createVoteDTO.getCreateTime());
            createdVoteVO.setUserid(createVoteDTO.getUserid());
            createdVoteVO.setName(createVoteDTO.getName());
            createdVoteVO.setState(createVoteDTO.getState());
            createdVoteVOList.add(createdVoteVO);
        }
        return new PageInfo<>(createdVoteVOList);
    }

    @Override
    public List<CreateVoteDTO> findAllByTitleAndUserid(String title, String userid) {
        return createVoteMapper.findAllByTitleAndUserid(title,userid);
    }

    @Override
    public String insertVote(CreateVoteVO createVoteVO) {
        CreateVoteDTO createVoteDTO = new CreateVoteDTO();
        String uuid32 = UuidUtil.getUUID32();
        createVoteDTO.setVid(uuid32);
        createVoteDTO.setTitle(createVoteVO.getTitle());
        createVoteDTO.setDetails(JSONUtils.toJSONString(createVoteVO.getDetails()));
        createVoteDTO.setCheckBox(createVoteVO.getCheckBox());
        createVoteDTO.setStartTime(createVoteVO.getStartTime());
        createVoteDTO.setEndTime(createVoteVO.getEndTime());
        createVoteDTO.setUserid(createVoteVO.getUserid());
        createVoteDTO.setName(createVoteVO.getName());
        createVoteDTO.setUserid_list(createVoteVO.getUserid_list());
        createVoteDTO.setDept_id_list(createVoteVO.getDept_id_list());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = null;
        try {
            now = sdf.parse(sdf.format(new Date()));
            long nowTime = now.getTime();
            long startTime = sdf.parse(sdf.format(createVoteDTO.getStartTime())).getTime();
            createVoteDTO.setCreateTime(now);
            if (nowTime < startTime){
                createVoteDTO.setState(0);
            }
            if (nowTime == startTime){
                createVoteDTO.setState(1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        将前台传过来的参数存入数据库
        createVoteMapper.insertVote(createVoteDTO);
//       发送工作通知
        if (createVoteDTO.getState() == 1){
            dingTalkUtils.sendWorkMessage(createVoteVO.getUserid_list(),createVoteVO.getDept_id_list(),createVoteVO.getTitle());
        }
        return uuid32;
    }

    @Override
    public CreateVoteDTO findByVid(String uuid32) {
        //        根据vid(投票标识)查出是哪一个投票
        return createVoteMapper.findByVid(uuid32);
    }


    @Override
    public ServiceResult userVoting(UserVotingVO userVotingVO) {
        CreateVoteDTO createVoteContent = createVoteMapper.findByVid(userVotingVO.getVid());

//        判断是否为多选框
        if(!createVoteContent.getCheckBox() ){
            ArrayList<Object> list = new ArrayList<>();
            for (String userVotingCheck : userVotingVO.getUserVotingChecks()) {
                if (userVotingCheck != "" || userVotingCheck.length()>0){
                    list.add(userVotingCheck);
                }
            }
            if (list.size()>1) {
                System.out.println("单选情况下，无法选个多个选项");
                return null;
            }
        }

//        判断投票人是否已经投票
        UserVotingPersonDTO VotingPersonDTO = userVotingPersonMapper.findByVidAndUserId(userVotingVO.getVid(),userVotingVO.getUserid());
        if (VotingPersonDTO != null){
            return ServiceResult.failure("请勿再次投票");
        }

//        判断投票是否截止
        long endtime = createVoteContent.getEndTime().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date now = null;
        try {
            now = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            long starttime = sdf.parse(sdf.format(now)).getTime();
            if (starttime > endtime){
                return ServiceResult.failure("截至投票");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        根据用户选项存入数据库
        for (String userVotingCheck : userVotingVO.getUserVotingChecks()) {
            if (!StringUtils.isNullOrEmpty(userVotingCheck)){
                UserVotingCheckDTO userVotingCheckDTO = new UserVotingCheckDTO();
                userVotingCheckDTO.setVid(userVotingVO.getVid());
                userVotingCheckDTO.setUserVotingStartTime(now);
                userVotingCheckDTO.setUserVotingCheck(userVotingCheck);
                userVotingCheckMapper.insertUserVoting(userVotingCheckDTO);
            }
        }
//        根据用户ID和用户名称存入数据库
        UserVotingPersonDTO userVotingPersonDTO = new UserVotingPersonDTO();
        userVotingPersonDTO.setVid(userVotingVO.getVid());
        userVotingPersonDTO.setUserVotingStartTime(now);
        userVotingPersonDTO.setUserVotingChecks(JSONUtils.toJSONString(userVotingVO.getUserVotingChecks()));
        userVotingPersonDTO.setUserid(userVotingVO.getUserid());
        userVotingPersonDTO.setName(userVotingVO.getName());
        userVotingPersonMapper.insertUserVoting(userVotingPersonDTO);
        return ServiceResult.success("用户投票成功");

////        用户投票结束后查询结果
//        HashMap<Object, Object> map = new HashMap<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<String> list = new ArrayList<>();
//        try {
//             list = objectMapper.readValue(createVoteContent.getDetails(), new ArrayList<String>().getClass());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for (int i = 0; i < list.size(); i++) {
//            List<String> stringList = userVotingCheckMapper.findByUserVotingCheck(userVotingVO.getVid(),list.get(i));
//            map.put(i,stringList.size());
//        }
//        UserVotingResult userVotingResult = new UserVotingResult();
//        userVotingResult.setDetailsNumber(map);
//        List<String> userIds = userVotingPersonMapper.findByVid(userVotingVO.getVid());
//        userVotingResult.setJoinedVoteNumber(userIds.size());
//        return userVotingResult;



//        UserVotingDTO userVotingDTO = new UserVotingDTO();
//        userVotingDTO.setVid(userVotingVO.getVid());
//        userVotingDTO.setUserId(userVotingVO.getUserId());
//        userVotingDTO.setUserVotingStartTime(now);
////            List<String> userVotingChecks = userVotingDTO.getUserVotingChecks();
//        ArrayList<String> list = new ArrayList<>();
//        for (String userVotingCheck : userVotingVO.getUserVotingChecks()) {
//            list.add(userVotingCheck);
//        }
//        userVotingDTO.setUserVotingChecks(JSONUtils.toJSONString(list));
//        userVotingMapper.insertUserVoting(userVotingDTO);


////        用户投票结束后查询结果
//        UserVoting userVoting = new UserVoting();
//        ArrayList<Object> list1 = new ArrayList<>();
//        ArrayList<Object> list2 = new ArrayList<>();
//        List<UserVotingDTO> userVotingDTOS = userVotingMapper.findByVid(userVotingVO.getVid());
//        for (UserVotingDTO userVotingDTO1 : userVotingDTOS) {
//            list1.add(userVotingDTO1.getUserId());
//            JSONArray array = JSONObject.parseArray(userVotingDTO1.getUserVotingChecks());
//            for (int i = 0; i < array.size(); i++) {
//               list2.add(array.get(i));
//            }
//        }
//        userVoting.setParticipated(list1);
//        userVoting.setVotingResult(list2);
//        return userVoting;
    }

    @Override
    public UserVotingResult viewVote(String vid) {
        CreateVoteDTO createVoteContent = createVoteMapper.findByVid(vid);
        //        用户投票结束后查询结果
        HashMap<Object, Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(createVoteContent.getDetails(), new ArrayList<String>().getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            List<String> stringList = userVotingCheckMapper.findByUserVotingCheck(vid,list.get(i));
            map.put(i,stringList.size());
        }
        UserVotingResult userVotingResult = new UserVotingResult();
        userVotingResult.setDetailsNumber(map);
        return userVotingResult;
    }

    @Override
    public UserVotingResultStatistics1 votingResultStatistics(String vid, String userid) {
       CreateVoteDTO createVoteContent = createVoteMapper.findByUserIdAndVid(vid,userid);
        if (createVoteContent == null) {
            System.out.println("用户还未创建投票");
            return null;
        }
        //        用户投票结束后查询结果
        HashMap<Object, Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(createVoteContent.getDetails(), new ArrayList<String>().getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            List<String> stringList = userVotingCheckMapper.findByUserVotingCheck(vid,list.get(i));
            map.put(i,stringList.size());
        }
        UserVotingResultStatistics1 userVotingResultStatistics1 = new UserVotingResultStatistics1();
        userVotingResultStatistics1.setDetailsNumber(map);
        List<String> userIds = userVotingPersonMapper.findByVid(vid);
        userVotingResultStatistics1.setJoinedVoteNumber(userIds.size());
        List<String> useridList = Arrays.asList(createVoteContent.getUserid_list().split(","));
        userVotingResultStatistics1.setNoJoinedVoteNumber(useridList.size() - userIds.size());
        return userVotingResultStatistics1;
    }

    @Override
    public UserVotingResultStatistics2 votingResultStatistics2(String vid, String userid) {
        CreateVoteDTO createVoteContent = createVoteMapper.findByUserIdAndVid(vid,userid);
        if (createVoteContent == null) {
            System.out.println("用户还未创建投票");
            return null;
        }
        //        用户投票结束后查询结果
        HashMap<Object, Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(createVoteContent.getDetails(), new ArrayList<String>().getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            List<String> stringList = userVotingCheckMapper.findByUserVotingCheck(vid,list.get(i));
            map.put(i,stringList.size());
        }
        UserVotingResultStatistics2 userVotingResultStatistics2 = new UserVotingResultStatistics2();
        userVotingResultStatistics2.setDetailsNumber(map);

        List<JoinedVoteNumber> joinedVoteNumbers = userVotingPersonMapper.findByVid2(vid);
        List<String> list1 = new ArrayList<>();
        for (JoinedVoteNumber joinedVoteNumber : joinedVoteNumbers) {
            list1.add(joinedVoteNumber.getUserId());
        }
        userVotingResultStatistics2.setJoinedVoteNumber(list1);
        List<String> userIdList = new ArrayList<>(Arrays.asList(createVoteContent.getUserid_list().split(",")));
        userIdList.removeAll(list1);
        System.out.println(userIdList);
        userVotingResultStatistics2.setNoJoinedVoteNumber(userIdList);
        return userVotingResultStatistics2;
    }

    @Override
    public ServiceResult downloadVotingResult(String vid, String userid) {
        CreateVoteDTO createVoteContent = createVoteMapper.findByUserIdAndVid(vid,userid);
        if (createVoteContent == null) {
            System.out.println("用户还未创建投票");
            return ServiceResult.failure("用户还未创建投票");
        }
        List<UserVotingPersonExcelDTO> userVotingPersonExcelDTOList = userVotingPersonMapper.findVotingResultByVid(vid);
        String path = "D:\\wfb\\";
        long time = new Date().getTime();
        // 写法1
//        String fileName = path + "easyExcel01_write.xlsx";
        String fileName = path + time + "wfb1_write.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, UserVotingPersonExcelDTO.class).sheet("UserVotingPersonExcelDTO").doWrite(userVotingPersonExcelDTOList);
        return ServiceResult.success("下载成功");
    }

}
