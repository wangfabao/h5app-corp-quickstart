package com.dingtalk.h5app.quickstart.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVoting implements Serializable {

    /**
     * 已参与人数
     */
    private List Participated;

    /**
     * 总投票数
     */
    private List votingResult;
}
