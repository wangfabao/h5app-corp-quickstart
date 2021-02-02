package com.dingtalk.h5app.quickstart.config;


import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.mysql.jdbc.StringUtils;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DingTalkUtils {

    @Autowired
    AppConfig appConfig;


    /**
     *获取token
     * @return
     */
    public String getToken() {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(appConfig.getAppKey());
        request.setAppsecret(appConfig.getAppSecret());
        request.setHttpMethod("GET");
        OapiGettokenResponse response = null;
        try {
            response = client.execute(request);
            return response.getAccessToken();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(response.getAccessToken());
        return response.getAccessToken();
    }

//    /**
//     * 根据手机号获取userId
//     *  调用本接口根据手机号获取用户的userid。
//     * @return
//     */
//    public String getByMobile(String mobile){
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/getbymobile");
//        OapiV2UserGetbymobileRequest req = new OapiV2UserGetbymobileRequest();
//        req.setMobile(mobile);
//        OapiV2UserGetbymobileResponse rsp = null;
//        try {
//            rsp = client.execute(req, getToken());
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//        System.out.println(rsp.getBody());
//        return rsp.getResult().getUserid();
//    }

    /**
     * 创建群会话
     *  调用本接口创建群会话。
     *  群会话消息是指可以调用接口创建企业群聊会话，然后可以以系统名义向群里推送群聊消息。
     *  owner:群主的userid，可通过根据手机号获取userid接口获取userid。该员工必须为会话useridlist的成员之一。
     */
    public String createChat(String owner, List<String> userIdList){
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/create");
        OapiChatCreateRequest req = new OapiChatCreateRequest();
        req.setName("微应用Demo");
        req.setOwner(owner);
        req.setUseridlist(userIdList);
        OapiChatCreateResponse rsp = null;
        try {
            rsp = client.execute(req, getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return rsp.getChatid();
    }

    /**
     * 获取群会话信息
     *  调用本接口获取群设置和成员信息
     * @return
     */
    public String getChat(String chatId){
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/get");
        OapiChatGetRequest req = new OapiChatGetRequest();
        req.setChatid(chatId);
        req.setHttpMethod("GET");
        OapiChatGetResponse rsp = null;
        try {
            rsp = client.execute(req, getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return rsp.getBody();
    }

    /**
     * 发送消息到企业群
     */
    public void sendChat(String chatId,String msg){
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/send");
        OapiChatSendRequest req = new OapiChatSendRequest();
        req.setChatid(chatId);
        OapiChatSendRequest.Msg obj1 = new OapiChatSendRequest.Msg();
        OapiChatSendRequest.Text obj2 = new OapiChatSendRequest.Text();

        obj2.setContent(msg);
        obj1.setText(obj2);
        obj1.setMsgtype("text");
        req.setMsg(obj1);
        OapiChatSendResponse rsp = null;
        try {
            rsp = client.execute(req, getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
    }

    /**
     * 发送普通消息
     * sender:消息发送者的userid。
     * cid:群会话或者个人会话的id，通过JSAPI接口唤起联系人界面选择会话获取会话cid。调用前端API获取：
     * msg:消息内容，最长不超过2048个字节。消息类型和样例参考消息类型与数据格式。
     */
    public void sendCommonMessage(String userId,String cid){
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/message/send_to_conversation");

        OapiMessageSendToConversationRequest req = new OapiMessageSendToConversationRequest();
        req.setSender(userId);
        req.setCid(cid);
        OapiMessageSendToConversationRequest.Msg msg = new OapiMessageSendToConversationRequest.Msg();

// 文本消息
        OapiMessageSendToConversationRequest.Text text = new OapiMessageSendToConversationRequest.Text();
        text.setContent("测试测试");
        msg.setText(text);
        msg.setMsgtype("text");
        req.setMsg(msg);

// 图片
        OapiMessageSendToConversationRequest.Image image = new OapiMessageSendToConversationRequest.Image();
        image.setMediaId("@lADOdvRYes0CbM0CbA");
        msg.setImage(image);
        msg.setMsgtype("image");
        req.setMsg(msg);

// 文件
        OapiMessageSendToConversationRequest.File file = new OapiMessageSendToConversationRequest.File();
        file.setMediaId("@lADOdvRYes0CbM0CbA");
        msg.setFile(file);
        msg.setMsgtype("file");
        req.setMsg(msg);


        OapiMessageSendToConversationRequest.Markdown markdown = new OapiMessageSendToConversationRequest.Markdown();
        markdown.setText("# 这是支持markdown的文本 \\n## 标题2  \\n* 列表1 \\n![alt 啊](https://img.alicdn.com/tps/TB1XLjqNVXXXXc4XVXXXXXXXXXX-170-64.png)");
        markdown.setTitle("首屏会话透出的展示内容");
        msg.setMarkdown(markdown);
        msg.setMsgtype("markdown");
        req.setMsg(msg);


        OapiMessageSendToConversationRequest.ActionCard actionCard = new OapiMessageSendToConversationRequest.ActionCard();
        actionCard.setTitle("是透出到会话列表和通知的文案");
        actionCard.setMarkdown("持markdown格式的正文内");
        actionCard.setSingleTitle("查看详情");
        actionCard.setSingleUrl("https://open.dingtalk.com");
        msg.setActionCard(actionCard);
        msg.setMsgtype("action_card");
        req.setMsg(msg);

// link消息
        OapiMessageSendToConversationRequest.Link link = new OapiMessageSendToConversationRequest.Link();
        link.setMessageUrl("https://www.baidu.com/");
        link.setPicUrl("@lADOdvRYes0CbM0CbA");
        link.setText("测试");
        link.setTitle("oapi");
        msg.setLink(link);
        msg.setMsgtype("link");
        req.setMsg(msg);

        OapiMessageSendToConversationResponse response = null;
        try {
            response = client.execute(req, getToken());
            System.out.println(getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(response.getBody());
    }


    /**
     * 创建投票时发送工作消息
     * agent_id
     * userid_list:接收者的企业内部用户的userid列表，最大用户列表长度100。
     * dept_id_list:接收者的部门id列表，最大列表长度20。接收者是部门ID下包括子部门下的所有用户。
     * to_all_user:是否发送给企业全部用户,当设置为false时必须指定userid_list或dept_id_list其中一个参数的值。
     * msg:消息内容，最长不超过2048个字节。消息类型和样例参考消息类型与数据格式
     *
     * 同一个应用相同消息的内容同一个用户一天只能接收一次。
     * 同一个应用给同一个用户发送消息，企业内部应用一天不得超过500次。
     * 通过设置to_all_user参数全员推送消息，一天最多3次。
     * 超出以上限制次数后，接口返回成功，但用户无法接收到。详细的限制说明，请参考工作通知消息限制。
     * 该接口是异步发送消息，接口返回成功并不表示用户一定会收到消息，需要通过获取工作通知消息的发送结果接口查询是否给用户发送成功。
     * @return
     */
    public void sendWorkMessage(@RequestParam(value = "userid_list")String userid_list,
                                @RequestParam(value = "dept_id_list", required = false, defaultValue = "1")String dept_id_list,
                                String voteTitle){
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setAgentId(Long.valueOf(appConfig.getAgentId()));
        request.setUseridList(userid_list);
        if (StringUtils.isNullOrEmpty(dept_id_list)){
            dept_id_list = "1";
        }
        request.setDeptIdList(dept_id_list);
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent("test123");
        request.setMsg(msg);

        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
//        msg.getActionCard().setTitle("xxx1234111111111");
//        msg.getActionCard().setMarkdown("### 测试1231111111");
//        msg.getActionCard().setSingleTitle("测试测试11111");
        msg.getActionCard().setTitle("投票通知");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date nowTime = null;
        try {
            nowTime = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        msg.getActionCard().setMarkdown("投票通知当前时间:"+nowTime);
        msg.getActionCard().setSingleTitle(voteTitle);
        msg.getActionCard().setSingleUrl("https://www.baidu.com");
        msg.setMsgtype("action_card");
        request.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response rsp = null;
        try {
            rsp = client.execute(request, getToken());
            System.out.println(getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
    }

    /**
     * 获取通讯录权限范围
     */
    public void GetAddressBookPermissionRange(){
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/auth/scopes");
        OapiAuthScopesRequest req = new OapiAuthScopesRequest();
        req.setHttpMethod("GET");
        OapiAuthScopesResponse rsp = null;
        try {
            rsp = client.execute(req, getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
    }
    /**
     * 获取部门详情
     */
//    public void bumenxiangqing(){
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/get");
//        OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
//        req.setDeptId(1L);
//        req.setLanguage("zh_CN");
//        OapiV2DepartmentGetResponse rsp = null;
//        try {
//            rsp = client.execute(req, getToken());
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//        System.out.println(rsp.getBody());
////        return rsp.getResult();
//    }

    /**
     * 获取部门列表
     * Query参数
     * 名称               类型      是否必填    示例值
     * access_token     String      是       6ed1bxxx
     * lang             String      否       zh_CN
     * fetch_child      Boolean     否       true
     * id               String      否       1
     * 返回值：
     * {"errcode":0,"department":[{"createDeptGroup":true,"name":"投票测试部门","id":450442098,"autoAddUser":true,"parentid":1},
     * {"createDeptGroup":true,"name":"投票测试部门002","id":450922002,"autoAddUser":true,"parentid":1}],"errmsg":"ok"}
     */
    public List<OapiDepartmentListResponse.Department> GetDepartmentList(){
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
        OapiDepartmentListRequest req = new OapiDepartmentListRequest();
        req.setLang("zh_CN");
        req.setFetchChild(true);
        req.setId("1");
        req.setHttpMethod("GET");
        OapiDepartmentListResponse rsp = null;
        try {
            rsp = client.execute(req, getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return rsp.getDepartment();
    }

    /**
     * 获取部门id
     */
    public List<String> listDepid() {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
        OapiDepartmentListRequest request = new OapiDepartmentListRequest();
        request.setHttpMethod("GET");
        OapiDepartmentListResponse response;
        try {
            response = client.execute(request, getToken());
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "获取部门Id 失败");
        }
        System.out.println(response.getDepartment().stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList()));
        return response.getDepartment().stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
    }

    /**
     * 获取整个部门的userid
     * @param depid
     * @return
     */
    public List<String> listUserId(String depid) {
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/listid");
//        OapiUserListidRequest req = new OapiUserListidRequest();
//        req.setDeptId(100L);
//        OapiUserListidResponse rsp = client.execute(req, access_token);
//        System.out.println(rsp.getBody());

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getDeptMember");
        OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
        req.setDeptId(depid);
        req.setHttpMethod("GET");
        OapiUserGetDeptMemberResponse response;
        try {
            response = client.execute(req, getToken());
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "获取getUserIds失败");
        }
        System.out.println(response.getBody());
        return response.getUserIds();
    }


}
