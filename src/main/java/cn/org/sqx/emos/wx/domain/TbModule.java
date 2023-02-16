package cn.org.sqx.emos.wx.domain;


import java.io.Serializable;

import lombok.Data;

/**
 * 模块资源表
 *
 * @TableName tb_module
 */
@Data
public class TbModule implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private int id;
    /**
     * 模块编号
     */
    private String moduleCode;
    /**
     * 模块名称
     */
    private String moduleName;
}
