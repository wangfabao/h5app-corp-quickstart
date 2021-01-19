package com.dingtalk.h5app.quickstart.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotingResultStatisticsVO {

    /**
     * 已参与人数
     */
    private List Participated;

    /**
     * 总投票数
     */
    private List votingResult;
}
