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
public interface TbWorkdayMapper {
    //查询特殊工作日
    public Integer searchTodayIsWorkday();

    //查询指定时间内的工作日
    public ArrayList<String> searchWorkdayInRange(HashMap param);
}
