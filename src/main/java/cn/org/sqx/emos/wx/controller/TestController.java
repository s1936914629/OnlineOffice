package cn.org.sqx.emos.wx.controller;

import cn.org.sqx.emos.wx.common.util.R;
import cn.org.sqx.emos.wx.controller.from.TestSayHelloFrom;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @auther: sqx
 * @Date: 2022-11-12
 */
@RestController
@RequestMapping("/test")
@Api("测试web接口")
public class TestController {

    @PostMapping("/sayHello")
    @ApiOperation("测试方法sayHello")
    //用于验证注解是否符合要求，直接加在变量参数之前，在变量中添加验证信息的要求，当不符合要求时就会在方法中返回message 的错误提示信息
    public R sayHello(@Valid @RequestBody TestSayHelloFrom from) {
        return R.ok().put("message", "Hello,"+from.getName());
    }

    @PostMapping("/addUser")
    @ApiOperation("权限验证的测试方法")
    // @RequiresPermissions(value = {"ROOT","USER:ADD"},logical = Logical.OR)
    @RequiresPermissions(value = {"A","a"},logical = Logical.OR)
    public R addUser(){
        return R.ok("用户添加成功");
    }
}
