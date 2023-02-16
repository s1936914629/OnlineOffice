package cn.org.sqx.emos.wx.controller.from;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 客户端提交数据
 *
 * @auther: sqx
 * @Date: 2022-12-11
 */
@ApiModel
@Data
public class LoginForm {
    @NotBlank(message = "临时授权不能为空")
    private String code;
}
