package cn.org.sqx.emos.wx.controller.from;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分页请求数据
 *
 * @auther: sqx
 * @Date: 2023-02-11
 */

@ApiModel
@Data
public class SearchMyMeetingListByPageForm {
    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Range(min = 1, max = 40)
    private Integer length;
}
