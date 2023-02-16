package cn.org.sqx.emos.wx.domain;


import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @TableName tb_workday
 */
@Data
public class TbWorkday implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 日期
     */
    private Date date;
}
