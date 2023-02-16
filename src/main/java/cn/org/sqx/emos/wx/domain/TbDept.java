package cn.org.sqx.emos.wx.domain;



import java.io.Serializable;

import lombok.Data;

/**
 * @TableName tb_dept
 */
@Data
public class TbDept implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private int id;
    /**
     * 部门名称
     */
    private String deptName;
}
