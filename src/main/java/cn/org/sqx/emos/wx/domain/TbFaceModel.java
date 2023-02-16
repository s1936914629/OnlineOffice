package cn.org.sqx.emos.wx.domain;


import java.io.Serializable;

import lombok.Data;

/**
 * @TableName tb_face_model
 */

@Data
public class TbFaceModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键值
     */
    private int id;
    /**
     * 用户ID
     */
    private Object userId;
    /**
     * 用户人脸模型
     */
    private String faceModel;
}
