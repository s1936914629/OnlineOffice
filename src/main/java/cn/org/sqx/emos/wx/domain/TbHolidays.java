package cn.org.sqx.emos.wx.domain;


import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 节假日表
 *
 * @TableName tb_holidays
 */
@Data
public class TbHolidays implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private int id;
    /**
     * 日期
     */
    private Date date;
}
