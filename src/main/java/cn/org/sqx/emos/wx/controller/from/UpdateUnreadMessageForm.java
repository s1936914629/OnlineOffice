package cn.org.sqx.emos.wx.controller.from;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @auther: sqx
 * @Date: 2023-02-08
 */

@ApiModel
@Data
public class UpdateUnreadMessageForm {
    @NotBlank
    private String id;
}
