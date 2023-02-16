package cn.org.sqx.emos.wx.controller;

import cn.org.sqx.emos.wx.common.util.R;
import cn.org.sqx.emos.wx.config.shiro.JwtUtil;
import cn.org.sqx.emos.wx.controller.from.SearchMyMeetingListByPageForm;
import cn.org.sqx.emos.wx.service.impl.MeetingServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2023-02-11
 */

@RestController
@RequestMapping("/meeting")
@Api("会议模块网络接口")
public class MeetingController {
    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private MeetingServiceImpl meetingService;

    @PostMapping("/searchMyMeetingListByPage")
    @ApiOperation("查询会议列表分页数据")
    public R searchMyMeetingListByPage(@Valid @RequestBody SearchMyMeetingListByPageForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        Integer page = form.getPage();
        Integer length = form.getLength();
        long start = (page - 1) * length;

        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("start", start);
        map.put("length", length);

        ArrayList<HashMap> list = meetingService.searchMyMeetingListByPage(map);
        return R.ok().put("result", list);
    }

}
