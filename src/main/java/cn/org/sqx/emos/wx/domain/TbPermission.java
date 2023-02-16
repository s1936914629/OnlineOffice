package cn.org.sqx.emos.wx.domain;


import java.io.Serializable;

import lombok.Data;

/**
 * @TableName tb_permission
 */
@Data
public class TbPermission implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private int id;
    /**
     * 权限
     */
    private String permissionName;
    /**
     * 模块ID
     */
    private Object moduleId;
    /**
     * 行为ID
     */
    private Object actionId;
}
