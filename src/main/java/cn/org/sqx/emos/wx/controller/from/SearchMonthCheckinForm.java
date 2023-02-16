package cn.org.sqx.emos.wx.controller.from;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 接受月考勤数据
 *
 * @auther: sqx
 * @Date: 2023-01-09
 */
@Data
@ApiModel
public class SearchMonthCheckinForm {
    @NotNull
    @Range(min = 2000, max = 3000)
    private Integer year;

    @NotNull
    @Range(min = 1, max = 12)
    private Integer month;
}
