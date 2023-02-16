package cn.org.sqx.emos.wx.controller.from;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 小程序提交的签到数据
 *
 * @auther: sqx
 * @Date: 2023-01-02
 */
@Data
@ApiModel
public class CheckinForm {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}
