package cn.org.sqx.emos.wx.mapper;

import cn.org.sqx.emos.wx.domain.TbMeeting;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2023-02-11
 */

@Mapper
@Repository
public interface TbMeetingMapper {
    //分页查询会议列表
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    //增加会议记录
    public int insertMeeting(TbMeeting entity);


}
