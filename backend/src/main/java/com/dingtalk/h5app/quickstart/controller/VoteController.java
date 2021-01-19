package com.dingtalk.h5app.quickstart.controller;

import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import com.dingtalk.h5app.quickstart.VO.CreateVoteVO;
import com.dingtalk.h5app.quickstart.VO.UserVotingVO;
import com.dingtalk.h5app.quickstart.VO.ViewVoteVO;
import com.dingtalk.h5app.quickstart.VO.VotingResultStatisticsVO;
import com.dingtalk.h5app.quickstart.bean.AjaxMessage;
import com.dingtalk.h5app.quickstart.pojo.UserVoting;
import com.dingtalk.h5app.quickstart.service.VoteService;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
public class VoteController {

    @Autowired
    VoteService voteService;

    /**
     * 创建一个投票
     */
    @PostMapping("/createVote")
    public AjaxMessage createVote(@RequestBody CreateVoteVO createVoteVO) {
        if (StringUtils.isNullOrEmpty(createVoteVO.getTitle()) || (createVoteVO.getTitle().length() > 400)) {
            return new AjaxMessage(false, "投票标题参数异常");
        }
        if (createVoteVO.getDetails() != null && !createVoteVO.getDetails().isEmpty()) {
            for (String detail : createVoteVO.getDetails()) {
                if (detail == "" || detail.length() < 1) {
                    return new AjaxMessage(false, "请输入选项内容");
                }
            }
        }
        long endtime = createVoteVO.getEndtime().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date now = new Date();
        try {
            long starttime = sdf.parse(sdf.format(now)).getTime();
            if (starttime > endtime) {
                return new AjaxMessage(false, "截至日期不允许小于当前时间");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        CreateVoteDTO createVoteDTO = voteService.insertVote(createVoteVO);
        return new AjaxMessage(true, createVoteDTO);
    }


    /**
     * 用户投票
     * 传进来的参数有：是否允许多选，投票明细,截至时间，vid,userid
     * 先根据传进来的参数存到数据库再说
     * 然后拿出来比较
     * 返回的参数有：投票人数list，投票结果list
     * 未解决：
     * 多选情况：明细1，2，3...的票数，已经参与的人数,应参与人数？
     * 单选情况：明细1或2或3.。的票数，已经参与的人数
     */
    @RequestMapping("/userVoting")
    public UserVoting userVoting(@RequestBody UserVotingVO userVotingVO) {
        return voteService.userVoting(userVotingVO);
    }


    /**
     * 查看投票
     * 已参与投票的人员可以对应工作群和工作通知中查看投票信息（看不到具体的详情），还未投票的人员禁止查看
     * 传入参数：userId，vid
     * 返回参数:投票标题，投票结果list，是否多选，截至时间
     */
    @GetMapping("/viewVote")
    public AjaxMessage viewVote(String userId, String vid){

        try {
                ViewVoteVO viewVoteVO = voteService.viewVote(userId, vid);
            return new AjaxMessage(true,viewVoteVO);
        } catch (Exception e) {
            e.printStackTrace();
            return new AjaxMessage(false,"查看投票失败");
        }
    }


    /**
     * 投票结果统计
     * 发起者可以随时查看投票结果和投票人，也可以向未投票的人发起通知，允许导出下载
     * 传入参数：userId,vid
     * 返回参数：投票人数list，投票结果list
     */
    @GetMapping("/votingResultStatistics")
    public AjaxMessage votingResultStatistics(String userId, String vid){
        try {
            VotingResultStatisticsVO votingResultStatisticsVO = voteService.votingResultStatistics(userId,vid);
            return new AjaxMessage(true,votingResultStatisticsVO);
        } catch (Exception e) {
            e.printStackTrace();
            return new AjaxMessage(false,"投票结果统计失败");
        }

    }
}