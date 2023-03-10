package cn.org.sqx.emos.wx.controller;

import cn.org.sqx.emos.wx.common.util.R;
import cn.org.sqx.emos.wx.config.shiro.JwtUtil;
import cn.org.sqx.emos.wx.controller.from.DeleteMessageRefByIdForm;
import cn.org.sqx.emos.wx.controller.from.SearchMessageByIdForm;
import cn.org.sqx.emos.wx.controller.from.SearchMessageByPageForm;
import cn.org.sqx.emos.wx.controller.from.UpdateUnreadMessageForm;
import cn.org.sqx.emos.wx.service.impl.MessageServiceImpl;
import cn.org.sqx.emos.wx.task.MessageTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * 消息模块
 *
 * @auther: sqx
 * @Date: 2023-02-08
 */

@RestController
@RequestMapping("/message")
@Api("消息模块网络接口")
public class MessageController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageServiceImpl messageService;

    @Resource
    private MessageTask messageTask;

    /**
     * 获取分页消息列表
     *
     * @param form  前端数据
     * @param token 令牌
     * @return R对象
     */
    @PostMapping("/searchMessageByPage")
    @ApiOperation("获取分页消息列表")
    public R searchMessageByPage(@Valid @RequestBody SearchMessageByPageForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        int page = form.getPage();
        int length = form.getLength();
        long start = (page - 1) * length;
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return R.ok().put("result", list);
    }

    @PostMapping("/searchMessageById")
    @ApiOperation("根据ID查询消息")
    public R searchMessageById(@Valid @RequestBody SearchMessageByIdForm form) {
        HashMap map = messageService.searchMessageById(form.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/updateUnreadMessage")
    @ApiOperation("未读消息更新成已读消息")
    public R updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageForm form) {
        long rows = messageService.updateUnreadMessage(form.getId());
        return R.ok().put("result", rows == 1 ?true : false);
    }

    @PostMapping("/deleteMessageRefById")
    @ApiOperation("未读消息更新成已读消息")
    public R deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdForm form) {
        long rows = messageService.deleteMessageRefById(form.getId());
        return R.ok().put("result", rows == 1 ?true : false);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("轮询接收系统消息")
    public R refreshMessage(@RequestHeader("token") String token) {
        int id = jwtUtil.getUserId(token);
        //异步接收消息
        messageTask.receiveAsync(id + "");
        //查询接收了多少条消息
        long lastRows = messageService.searchLastCount(id);
        //查询未读数据
        long unreadRows = messageService.searchUnreadCount(id);

        return R.ok().put("lastRows", lastRows).put("unreadRows", unreadRows);

    }

}
