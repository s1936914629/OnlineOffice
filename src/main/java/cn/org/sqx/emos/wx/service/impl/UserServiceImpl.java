package cn.org.sqx.emos.wx.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.org.sqx.emos.wx.domain.MessageEntity;
import cn.org.sqx.emos.wx.domain.TbUser;
import cn.org.sqx.emos.wx.mapper.TbDeptMapper;
import cn.org.sqx.emos.wx.mapper.TbUserMapper;
import cn.org.sqx.emos.wx.exception.EmosException;
import cn.org.sqx.emos.wx.service.UserService;
import cn.org.sqx.emos.wx.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @auther: sqx
 * @Date: 2022-12-05
 */

@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private MessageTask messageTask;

    @Resource
    private TbDeptMapper deptMapper;

    /**
     * 注册新用户
     *
     * @param registerCode 激活码
     * @param code         临时授权
     * @param nickname     微信昵称
     * @param photo        微信头像的地址
     * @return 新用户的ID
     */
    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        //如果邀请码是 000000 代表是超级管理员
        if (registerCode.equals("000000")) {
            //查询是否已经存在超级管理员
            boolean b = userMapper.haveRootUser();
            if (!b) {
                //把当前用户绑定到ROOT账户
                String openId = getOpenId(code);
                HashMap<String, Object> param = new HashMap<>();

                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("root", true);
                param.put("status", 1);
                param.put("createTime", new Date());

                userMapper.insert(param);

                int id = userMapper.searchIdByOpenId(openId);

                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setSenderId(0);
                messageEntity.setSenderName("系统消息");
                messageEntity.setUuid(IdUtil.simpleUUID());
                messageEntity.setMsg("欢迎您注册成为超级管理员，请及时更新你的员工个人信息。");
                messageEntity.setSendTime(new Date());
                messageTask.sendAsync(id + "", messageEntity);

                return id;
            } else {
                throw new EmosException("无法绑定超级管理员");
            }
        } else {
            //TODO 普通员工注册
        }

        return 0;
    }

    /**
     * 查询用户的权限
     *
     * @param userId 用户的ID
     * @return 权限列表
     */
    @Override
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions = userMapper.searchUserPermissions(userId);
        return permissions;
    }

    /**
     * 查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public TbUser searchById(int userId) {
        return userMapper.searchById(userId);
    }

    /**
     * 登录方法
     *
     * @param code 临时授权字符串
     * @return 用户id
     */
    @Override
    public Integer login(String code) {
        String openId = getOpenId(code);
        Integer id = userMapper.searchIdByOpenId(openId);
        if (id == null) {
            throw new EmosException("账户不存在");
        }

        //TODO 从消息队列中接受数据，转移到消息表
        // messageTask.receiveAsync(id + "");

        return id;
    }

    /**
     * 查询员工的入职日期
     *
     * @param userId 用户Id
     * @return 入职日期
     */
    @Override
    public String searchUserHiredate(int userId) {
        return userMapper.searchUserHiredate(userId);
    }

    /**
     * 查询用户概要信息
     *
     * @param userId 用户Id
     * @return 前端显示的数据
     */
    @Override
    public HashMap searchUserSummary(int userId) {
        return userMapper.searchUserSummary(userId);
    }

    /**
     * 查询部门用户
     *
     * @param keyword 部门的编号
     * @return 部门用户
     */
    @Override
    public ArrayList<HashMap> searchUserGroupByDept(String keyword) {
        ArrayList<HashMap> list_1 = deptMapper.searchDeptMembers(keyword);      //部门数据
         ArrayList<HashMap> list_2 = userMapper.searchUserGroupByDept(keyword);  //员工数据

        //合并数据
        for (HashMap map_1 : list_1) {      //遍历部门记录
            long deptId = (long) map_1.get("id");
            ArrayList members = new ArrayList();    //部门里面的员工记录

            for (HashMap map_2 : list_2) {      //遍历员工记录
                long id = (long) map_2.get("deptId");
                if (deptId == id) {
                    members.add(map_2);
                }
            }
            map_1.put("members", members);
        }
        return list_1;

    }

    /**
     * 查询多个成员
     *
     * @param param 成员id
     * @return 多个成员
     */
    @Override
    public ArrayList<HashMap> searchMembers(List param) {
        ArrayList<HashMap> list = userMapper.searchMembers(param);
        return list;
    }

    //获取openid
    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap<String, Object> map = new HashMap<>();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        //发送封装好的请求
        String response = HttpUtil.post(url, map);

        //解析返回的response
        JSONObject json = JSONUtil.parseObj(response);
        //提取openid
        String openid = json.getStr("openid");

        if (openid == null || openid.length() == 0) {
            throw new RuntimeException("临时登录凭证错误");
        }

        return openid;
    }
}
