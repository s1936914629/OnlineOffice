package cn.org.sqx.emos.wx.service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2022-12-26
 */
public interface CheckinService {
    /**
     * 用户是否可以签到
     *
     * @param userId 用户ID
     * @param date   当天日期
     * @return 具体业务消息
     */
    public String validCanCheckIn(int userId, String date);

    /**
     * 签到方法
     *
     * @param param 封装好的小程序提交的签到数据
     */
    public void checkin(HashMap param);

    /**
     * 创建人脸模型
     *
     * @param userId 用户Id
     * @param path   图片路径
     */
    public void createFaceModel(int userId, String path);

    /**
     * 查询签到的结果
     *
     * @param userId 用户id
     * @return 用户签到信息
     */
    public HashMap searchTodayCheckin(int userId);

    /**
     * 查询用户的签到天数
     *
     * @param userId 用户id
     * @return 签到天数
     */
    public long searchCheckinDays(int userId);

    /**
     * 查询一周内，用户的考勤情况
     *
     * @param param 用户信息
     * @return 考勤情况
     */
    public ArrayList<HashMap> searchWeekCheckin(HashMap param);

    /**
     * 查询月考勤
     *
     * @param param 需要查询的对象
     * @return 考勤记录
     */
    public ArrayList<HashMap> searchMothCheckin(HashMap param);
}
