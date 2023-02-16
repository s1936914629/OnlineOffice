package cn.org.sqx.emos.wx.mapper;

import cn.org.sqx.emos.wx.domain.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @auther: sqx
 * @Date: 2022-12-10
 */
@Mapper
@Repository
public interface TbUserMapper {
    // TODO 查询
    // 查询是否为超级管理员
    public boolean haveRootUser();

    // 查询用户ID
    public Integer searchIdByOpenId(String openId);

    // 查询权限
    public Set<String> searchUserPermissions(int userId);

    // 查询用户信息
    public TbUser searchById(int userId);

    //查询员工的姓名与部门
    public HashMap searchNameAndDept(int userId);

    //查询员工的入职日期
    public String searchUserHiredate(int userId);

    //查询用户概要信息
    public HashMap searchUserSummary(int userId);

    //查询部门用户
    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    // 查询多个成员
    public ArrayList<HashMap> searchMembers(List param);


    // TODO 新增
    // 添加保存的用户
    public int insert(HashMap param);
}
