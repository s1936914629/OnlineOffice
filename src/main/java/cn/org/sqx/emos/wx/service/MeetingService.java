package cn.org.sqx.emos.wx.service;

import cn.org.sqx.emos.wx.domain.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2023-02-11
 */
public interface MeetingService {
    /**
     * 分页查询会议列表
     * @param param 查询需要的参数
     * @return 会议列表
     */
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    /**
     * 插入会议记录
     * @param entity    会议实体类
     */
    public void insertMeeting(TbMeeting entity);
}
