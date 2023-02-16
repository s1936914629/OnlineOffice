package cn.org.sqx.emos.wx.domain;


import java.io.Serializable;

import lombok.Data;

/**
 * 行为表
 *
 * @TableName tb_action
 */
@Data
public class TbAction implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private int id;
    /**
     * 行为编号
     */
    private String actionCode;
    /**
     * 行为名称
     */
    private String actionName;
}
