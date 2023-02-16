package cn.org.sqx.emos.wx.service.impl;

import cn.hutool.json.JSONArray;
import cn.org.sqx.emos.wx.domain.TbMeeting;
import cn.org.sqx.emos.wx.exception.EmosException;
import cn.org.sqx.emos.wx.mapper.TbMeetingMapper;
import cn.org.sqx.emos.wx.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2023-02-11
 */
@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {
    @Resource
    private TbMeetingMapper meetingMapper;

    //分页查询会议列表
    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param) {
        ArrayList<HashMap> list = meetingMapper.searchMyMeetingListByPage(param);
        String date = null;
        ArrayList resultList = new ArrayList(); //会议列表
        HashMap resultMap = null;   //会以列表中的记录
        JSONArray array =null;      //放置记录
        for (HashMap map : list) {
            String temp = map.get("date").toString();
            //如果不是同一天，则新建一个会议列表
            if (!temp.equals(date)) {
                date = temp;
                resultMap = new HashMap();
                resultMap.put("date", date);
                array = new JSONArray();
                resultMap.put("list", array);
                resultList.add(resultMap);
            }
            array.put(map);
        }
        return resultList;
    }

    //插入会议记录
    @Override
    public void insertMeeting(TbMeeting entity) {
        //保存数据
        int row = meetingMapper.insertMeeting(entity);

        if (row != 1) {
            throw new EmosException("会议添加失败");
        }

        //TODO 开启审批工作流

    }
}
