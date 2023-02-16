package cn.org.sqx.emos.wx.service;

import cn.org.sqx.emos.wx.domain.TbUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @auther: sqx
 * @Date: 2022-12-05
 */
public interface UserService {

    /**
     * 注册新用户
     *
     * @param registerCode 激活码
     * @param code         临时授权
     * @param nickname     微信昵称
     * @param photo        微信头像的地址
     * @return 新用户的ID
     */
    public int registerUser(String registerCode, String code, String nickname, String photo);

    /**
     * 查询用户的权限
     *
     * @param userId 用户的ID
     * @return 权限列表
     */
    public Set<String> searchUserPermissions(int userId);

    /**
     * 查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public TbUser searchById(int userId);

    /**
     * 登录方法
     *
     * @param code 临时授权字符串
     * @return 用户id
     */
    public Integer login(String code);

    /**
     * 查询员工的入职日期
     *
     * @param userId 用户Id
     * @return 入职日期
     */
    public String searchUserHiredate(int userId);

    /**
     * 查询用户概要信息
     *
     * @param userId 用户Id
     * @return 前端显示的数据
     */
    public HashMap searchUserSummary(int userId);

    /**
     * 查询部门用户
     *
     * @param keyword 部门的编号
     * @return 部门用户
     */
    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    /**
     * 查询多个成员
     *
     * @param param 成员id
     * @return 多个成员
     */
    public ArrayList<HashMap> searchMembers(List param);
}
