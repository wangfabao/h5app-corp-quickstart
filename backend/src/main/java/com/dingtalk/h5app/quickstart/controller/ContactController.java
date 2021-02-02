package com.dingtalk.h5app.quickstart.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.request.OapiUserSimplelistRequest;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse.Department;
import com.dingtalk.api.response.OapiUserSimplelistResponse;
import com.dingtalk.api.response.OapiUserSimplelistResponse.Userlist;
import com.dingtalk.h5app.quickstart.config.AppConfig;
import com.dingtalk.h5app.quickstart.config.ListToTree;
import com.dingtalk.h5app.quickstart.domain.DepartmentDTO;
import com.dingtalk.h5app.quickstart.domain.ServiceResult;
import com.dingtalk.h5app.quickstart.domain.UserDTO;
import com.dingtalk.h5app.quickstart.service.TokenService;
import com.taobao.api.ApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.dingtalk.h5app.quickstart.config.UrlConstant.URL_DEPARTMENT_LIST;
import static com.dingtalk.h5app.quickstart.config.UrlConstant.URL_USER_SIMPLELIST;

/**
 * 微应用QuickStart示例，访问联系人API
 *
 * @author openapi@dingtalk
 * @date 2020/2/4
 */
@RestController
@CrossOrigin("/contact")
//@CrossOrigin("*") // NOTE：此处仅为本地调试使用，为避免安全风险，生产环境请勿设置CORS为 '*'
public class ContactController {
    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private TokenService tokenService;
    private AppConfig appConfig;
    
    @Autowired
    public ContactController(
        TokenService tokenService,
        AppConfig appConfig
    ) {
        this.tokenService = tokenService;
        this.appConfig = appConfig;
    }

    /**
     * 获取部门列表
     * @param id ：父部门ID。如果不传，默认部门为根部门，根部门ID为1。
     * @return
     */
    @GetMapping("/department/list")
//    public ServiceResult<List<DepartmentDTO>> listDepartment(
    public Serializable listDepartment(
        @RequestParam(value = "id", required = false, defaultValue = "1") String id
    ) {
        String accessToken;
        // 获取accessToken
        ServiceResult<String> accessTokenSr = tokenService.getAccessToken();
        if (!accessTokenSr.isSuccess()) {
            return ServiceResult.failure(accessTokenSr.getCode(), accessTokenSr.getMessage());
        }
        accessToken = accessTokenSr.getResult();

        DingTalkClient client = new DefaultDingTalkClient(URL_DEPARTMENT_LIST);
        OapiDepartmentListRequest request = new OapiDepartmentListRequest();
        request.setFetchChild(true);    //是否递归部门的全部子部门。
        request.setId(id);
        request.setHttpMethod("GET");

        OapiDepartmentListResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            log.error("Failed to {}", URL_DEPARTMENT_LIST, e);
            return ServiceResult.failure(e.getErrCode(), "Failed to listDepartment: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ServiceResult.failure(response.getErrorCode(), response.getErrmsg());
        }

        if (CollectionUtils.isNotEmpty(response.getDepartment())) {
            List<DepartmentDTO> results = new ArrayList<>(response.getDepartment().size());
            for (Department department : response.getDepartment()) {
                DepartmentDTO departmentDTO = new DepartmentDTO();
                departmentDTO.setId(department.getId());
                departmentDTO.setName(department.getName());
                departmentDTO.setCreateDeptGroup(department.getCreateDeptGroup());
                departmentDTO.setAutoAddUser(department.getAutoAddUser());
                departmentDTO.setParentid(department.getParentid());

                departmentDTO.setTitle(department.getName());
                departmentDTO.setKey(department.getId());
                departmentDTO.setValue(department.getId());
                results.add(departmentDTO);
            }
            JSONArray tree = ListToTree.getTree(JSONArray.parseArray(JSON.toJSONString(results)), "id", "parentid", "children");
//            return ServiceResult.success(results);
            return ServiceResult.success(tree);
        }
        return ServiceResult.success(Collections.emptyList());
    }

    /**
     * 获取部门用户
     * @param id :获取部门用户的id
     * @param offset :支持分页查询，与size参数同时设置时才生效，此参数代表偏移量，偏移量从0开始。
     * @param size :支持分页查询，与offset参数同时设置时才生效，此参数代表分页大小，最大100。
     * @param order :支持分页查询，部门成员的排序规则，默认不传是按自定义排序：entry_asc：代表按照进入部门的时间升序
     * @return
     */
    @GetMapping("/user/simplelist")
    public ServiceResult<List<UserDTO>> listDepartmentUsers(
        @RequestParam("department_id") Long id,
        @RequestParam(name = "offset", required = false, defaultValue = "0") Long offset,
        @RequestParam(name = "size", required = false, defaultValue = "50") Long size,
        @RequestParam(name = "order", required = false, defaultValue = "entry_desc") String order
    ) {
        String accessToken;
        // 获取accessToken
        ServiceResult<String> accessTokenSr = tokenService.getAccessToken();
        if (!accessTokenSr.isSuccess()) {
            return ServiceResult.failure(accessTokenSr.getCode(), accessTokenSr.getMessage());
        }
        accessToken = accessTokenSr.getResult();

        DingTalkClient client = new DefaultDingTalkClient(URL_USER_SIMPLELIST);
        OapiUserSimplelistRequest request = new OapiUserSimplelistRequest();
        request.setDepartmentId(id);
        request.setOffset(offset);
        request.setSize(size);
        request.setOrder(order);
        request.setHttpMethod("GET");

        OapiUserSimplelistResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            log.error("Failed to {}", URL_DEPARTMENT_LIST, e);
            return ServiceResult.failure(e.getErrCode(), "Failed to listDepartment: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ServiceResult.failure(response.getErrorCode(), response.getErrmsg());
        }

        if (CollectionUtils.isNotEmpty(response.getUserlist())) {
            List<UserDTO> results = new ArrayList<>(response.getUserlist().size());
            for (Userlist userlist : response.getUserlist()) {
                UserDTO user = new UserDTO();
                user.setUserid(userlist.getUserid());
                user.setName(userlist.getName());
                results.add(user);
            }
            return ServiceResult.success(results);
        }
        return ServiceResult.success(Collections.emptyList());
    }
}
