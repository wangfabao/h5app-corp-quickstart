package com.dingtalk.h5app.quickstart.controller;

import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import com.dingtalk.h5app.quickstart.VO.*;
import com.dingtalk.h5app.quickstart.bean.TableData;
import com.dingtalk.h5app.quickstart.config.DingTalkUtils;
import com.dingtalk.h5app.quickstart.domain.ServiceResult;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResult;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResultStatistics1;
import com.dingtalk.h5app.quickstart.pojo.UserVotingResultStatistics2;
import com.dingtalk.h5app.quickstart.service.VoteService;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api")
public class VoteController {

    @Autowired
    VoteService voteService;

   @Autowired
   DingTalkUtils dingTalkUtils;

    @RequestMapping("/chatMessage")
    public void chatMessage(){
////        String mobile="18268128373";
//////        根据手机号获取userId
////        String userId = dDingUtilS.getByMobile(mobile);
//        System.out.println(userId);
////        获取部门id列表
//        List<String> stringList = dDingUtilS.listDepid();
////        获取整个部门的userId
//        List<String> stringList1 = dDingUtilS.listUserId(stringList.get(1));
////        创建群会话  chatId:chat66b81eecfed11aee1d3f533ff2272e6e
//        String chatId = dDingUtilS.createChat(userId,stringList1);
////        获取群会话信息,返回给前端
//        String msg = dDingUtilS.getChat(chatId);
////        发送消息到企业群
//        dDingUtilS.sendChat(chatId,msg);
        System.out.println("dw");
    }


    @RequestMapping("/mailList")
    public List mailList(){
////        获取token
//        System.out.println(dingTalkUtils.getToken());
////        获取通讯录权限范围
//        dingTalkUtils.GetAddressBookPermissionRange();
//        获取部门列表
//        return dDingUtilS.GetDepartmentList();
//        获取部门id列表
//        List<String> stringList = dingTalkUtils.listDepid();
////        获取整个部门的userId
//        dingTalkUtils.listUserId(stringList.get(1));
//       return dingTalkUtils.bumenxiangqing();
        return null;

    }

    /**
     * 创建人查看已创建的投票
     * @return
     */
    @GetMapping("/listCreatedVotes")
    public TableData listCreatedVotes(@RequestParam(name = "page", defaultValue = "1")Integer page,
                                      @RequestParam(name = "limit", defaultValue = "5")Integer limit,
                                      String title, String userid){
        PageInfo<CreatedVoteVO> pageInfo = voteService.findByTitleAndUserid(title,userid,page,limit);
        List<CreateVoteDTO> createVoteDTOList = voteService.findAllByTitleAndUserid(title,userid);
//        return new TableData(pageInfo.getTotal(),pageInfo.getList());
        return new TableData(createVoteDTOList.size(),pageInfo.getList());
    }

    /**
     * 创建一个投票
     */
    @PostMapping("/createVote")
    public ServiceResult createVote(@RequestBody CreateVoteVO createVoteVO) {
        if (StringUtils.isNullOrEmpty(createVoteVO.getTitle()) || (createVoteVO.getTitle().length() > 400)) {
            return ServiceResult.failure("投票标题参数异常");
        }
        if (createVoteVO.getDetails() != null && !createVoteVO.getDetails().isEmpty()) {
            for (String detail : createVoteVO.getDetails()) {
                if (detail == "" || detail.length() < 1) {
                    return ServiceResult.failure("请输入选项内容");
                }
            }
        }
        long startTime = createVoteVO.getStartTime().getTime();
        long endTime = createVoteVO.getEndTime().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date now = new Date();
        try {
            long crateTime = sdf.parse(sdf.format(now)).getTime();
            if (crateTime >startTime){
                return ServiceResult.failure("开始时间不允许小于创建时间");
            }
            if (crateTime > endTime) {
                return ServiceResult.failure("截至日期不允许小于创建时间");
            }
            if (startTime > endTime){
                return ServiceResult.failure("开始时间不允许小于截至日期");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ServiceResult.success(voteService.insertVote(createVoteVO));
    }

    /**
     * 根据vid(投票标识)查出是哪一个投票
     */
    @GetMapping("/findByVid")
    public ServiceResult findByVid(@RequestParam("vid") String uuid32){
        CreateVoteDTO createVoteDTO = voteService.findByVid(uuid32);
        return ServiceResult.success(createVoteDTO);
    }

    /**
     * 用户投票
     * 传进来的参数有：投票明细,截至时间，vid,userid
     * 先根据传进来的参数存到数据库再说
     */
    @RequestMapping("/userVoting")
    public ServiceResult userVoting(@RequestBody UserVotingVO userVotingVO) {
        try {
            return voteService.userVoting(userVotingVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServiceResult.failure("用户投票失败");
    }

    /**
     * 用户投票结束后查询结果
     *  传入参数:vid
     *  返回参数：用户投票票数 DetailsNumber--Map结构
     */
    @GetMapping("/viewVote")
    public ServiceResult<UserVotingResult> viewVote(String vid){
        try {
            UserVotingResult userVotingResult = voteService.viewVote(vid);
            return ServiceResult.success(userVotingResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServiceResult.failure("用户投票结束后查询结果失败");
    }

    /**
     * 投票结果统计1
     *  传入参数：vid userid
     */
    @GetMapping("/votingResultStatistics")
    public ServiceResult<UserVotingResultStatistics1> votingResultStatistics(String vid, String userid){
        UserVotingResultStatistics1 userVotingResultStatistics1 = voteService.votingResultStatistics(vid, userid);
        return ServiceResult.success(userVotingResultStatistics1);
    }

    /**
     * 投票结果统计2
     *  传入参数：vid userid
     */
    @GetMapping("/votingResultStatistics2")
    public ServiceResult<UserVotingResultStatistics2> votingResultStatistics2(String vid, String userid){
        UserVotingResultStatistics2 userVotingResultStatistics2 = voteService.votingResultStatistics2(vid, userid);
        return ServiceResult.success(userVotingResultStatistics2);
    }



    /**
     * 发起者可以随时查看投票结果和投票人，也可以向未投票的人发起通知，允许导出下载
     */
    @RequestMapping("/downloadVotingResult")
    public ServiceResult downloadVotingResult(String vid,String userid){

       return voteService.downloadVotingResult(vid,userid);
    }
}