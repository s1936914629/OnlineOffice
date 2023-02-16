package cn.org.sqx.emos.wx.mapper;

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
public interface TbHolidaysMapper {
    //查询特殊休息日
    public Integer searchTodayIsHolidays();

    //查询指定时间内的休闲日
    public ArrayList<String> searchHolidaysInRange(HashMap param);
}
