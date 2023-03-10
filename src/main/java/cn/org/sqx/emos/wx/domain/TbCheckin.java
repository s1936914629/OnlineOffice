package cn.org.sqx.emos.wx.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 签到表
 *
 * @TableName tb_checkin
 */
@Data
public class TbCheckin implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private int id;
    /**
     * 用户ID
     */
    private Object userId;
    /**
     * 签到地址
     */
    private String address;
    /**
     * 国家
     */
    private String country;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区划
     */
    private String district;
    /**
     * 考勤结果
     */
    private Byte status;
    /**
     * 风险等级
     */
    private Object risk;
    /**
     * 签到日期
     */
    private String  date;
    /**
     * 签到时间
     */
    private Date createTime;
}
