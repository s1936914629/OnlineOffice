package cn.org.sqx.emos.wx.mapper;

import cn.org.sqx.emos.wx.domain.TbCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2022-12-25
 */
@Mapper
@Repository
public interface TbCheckinMapper {

    //TODO 查询

    /**
     * 查询是否已签到
     * @param param 封装的数据
     * @return  有无记录
     */
    public Integer haveCheckin(HashMap param);

    /**
     * 查询签到的结果
     * @param userId 用户id
     * @return 用户签到信息
     */
    public HashMap searchTodayCheckin(int userId);

    /**
     * 查询用户的签到天数
     * @param userId 用户id
     * @return  签到天数
     */
    public long searchCheckinDays(int userId);

    /**
     * 查询一周内，用户的考勤情况
     * @param param 用户信息
     * @return  考勤情况
     */
    public ArrayList<HashMap> searchWeekCheckin(HashMap param);

    //TODO 新增
    /**
     * 签到数据
     * @param tbCheckin 签到表
     */
    public void insert(TbCheckin tbCheckin);
}
