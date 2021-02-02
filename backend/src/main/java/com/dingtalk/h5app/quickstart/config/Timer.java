package com.dingtalk.h5app.quickstart.config;

import com.dingtalk.h5app.quickstart.DTO.CreateVoteDTO;
import com.dingtalk.h5app.quickstart.mapper.CreateVoteMapper;
import com.dingtalk.h5app.quickstart.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Configuration
@EnableScheduling
public class Timer {

    @Autowired
    CreateVoteMapper createVoteMapper;

    @Autowired
    DingTalkUtils dingTalkUtils;

//    每天的早上9点发送创建投票的工作消息
    @Scheduled(cron = "0 0 9 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void checkCreateVote(){
        System.out.println("任务执行");
        List<CreateVoteDTO> createVoteDTOList = createVoteMapper.findAllCreateVote();
        for (CreateVoteDTO createVoteDTO : createVoteDTOList) {
            long endTime = createVoteDTO.getEndTime().getTime();
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
            try {
                long startTime = sdf.parse(sdf.format(now)).getTime();
                if (endTime>startTime){
                    dingTalkUtils.sendWorkMessage(createVoteDTO.getUserid_list(),createVoteDTO.getDept_id_list(),createVoteDTO.getTitle());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 每分钟扫描一次，看是否有待启动的投票，或待结束的投票
     *  需求：1.如果开始投票时间不等于创建投票时间，工作通知在开始投票时间发送
     *       2.开始时间和结束时间 > 24小时,工作通知每天通知
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void checkVote(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = null;    //当前时间
        try {
            now = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<CreateVoteDTO> createVoteStartDTOS = createVoteMapper.findByStartTime(now);
        List<CreateVoteDTO> createVoteEndDTOS = createVoteMapper.findByEndTime(now);
        if (createVoteStartDTOS != null){
            for (CreateVoteDTO createVoteDTO : createVoteStartDTOS) {
                System.out.println("投票状态--执行中");
                createVoteDTO.setState(1);
                createVoteMapper.updateState(createVoteDTO.getState(),createVoteDTO.getVid());
                dingTalkUtils.sendWorkMessage(createVoteDTO.getUserid_list(),createVoteDTO.getDept_id_list(),createVoteDTO.getTitle());
            }
        }
        if (createVoteEndDTOS != null){
            for (CreateVoteDTO createVoteEndDTO : createVoteEndDTOS) {
                System.out.println("投票状态--已结束");
                createVoteEndDTO.setState(2);
            }
        }
        List<CreateVoteDTO> createVoteStateDTOS = createVoteMapper.findByState(1);
        for (CreateVoteDTO createVoteStateDTO : createVoteStateDTOS) {
            if ((now.getTime() - createVoteStateDTO.getStartTime().getTime())%86400000 == 0) {
                dingTalkUtils.sendWorkMessage(createVoteStateDTO.getUserid_list(), createVoteStateDTO.getDept_id_list(), createVoteStateDTO.getTitle());
            }
        }
    }

}
