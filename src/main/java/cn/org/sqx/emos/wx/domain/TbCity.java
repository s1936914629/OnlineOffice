package cn.org.sqx.emos.wx.domain;


import java.io.Serializable;

import lombok.Data;

/**
 * 疫情城市列表
 *
 * @TableName tb_city
 */
@Data
public class TbCity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private int id;
    /**
     * 城市名称
     */
    private String city;
    /**
     * 拼音简称
     */
    private String code;
}
