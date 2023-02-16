package cn.org.sqx.emos.wx.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @auther: sqx
 * @Date: 2022-12-25
 */
@Data
@Component
public class SystemConstants {
    public String attendanceStartTime;  //上班考勤开始时间
    public String attendanceTime;       //上班时间
    public String attendanceEndTime;    //上班考勤截止时间
    public String closingStartTime;     //下班考勤开始时间
    public String closingTime;          //下班时间
    public String closingEndTime;       //下班考勤截止时间

}
