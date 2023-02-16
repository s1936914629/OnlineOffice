package cn.org.sqx.emos.wx.controller.from;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @auther: sqx
 * @Date: 2022-11-12
 */

@ApiModel
@Data
public class TestSayHelloFrom {

    // @NotBlank   //不能为空
    // @Pattern(regexp = "^[\\u4e00-\\u9f5a]{2,15}$")  //配置正则
    @ApiModelProperty("姓名")
    private String name;
}
