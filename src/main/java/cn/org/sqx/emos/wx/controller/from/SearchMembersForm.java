package cn.org.sqx.emos.wx.controller.from;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @auther: sqx
 * @Date: 2023-02-12
 */

@Data
@ApiModel
public class SearchMembersForm {
    @NotBlank
    private String members;
}
