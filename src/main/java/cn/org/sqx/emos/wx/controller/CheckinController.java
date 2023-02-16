package cn.org.sqx.emos.wx.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.org.sqx.emos.wx.common.util.R;
import cn.org.sqx.emos.wx.config.SystemConstants;
import cn.org.sqx.emos.wx.config.shiro.JwtUtil;
import cn.org.sqx.emos.wx.controller.from.CheckinForm;
import cn.org.sqx.emos.wx.controller.from.SearchMonthCheckinForm;
import cn.org.sqx.emos.wx.exception.EmosException;
import cn.org.sqx.emos.wx.service.UserService;
import cn.org.sqx.emos.wx.service.impl.CheckinServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 签到模块
 *
 * @auther: sqx
 * @Date: 2022-12-26
 */
@RequestMapping("/checkin")
@RestController
@Slf4j
@Api("签到模块Web接口")
public class CheckinController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinServiceImpl checkinService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConstants systemConstants;

    @Value("${emos.image-folder}")
    private String imageFolder;

    /**
     * 查看用户今天是否签到
     *
     * @param token 令牌
     * @return R对象
     */
    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户今天是否签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId, DateUtil.today());
        return R.ok(result);
    }

    /**
     * 签到
     *
     * @param form  签到提交的数据
     * @param file  上传的文件名必须使photo
     * @param token 令牌
     * @return R对象
     */
    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinForm form, @RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传文件");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("必须提交jpg格式图片");
        } else {
            String path = imageFolder + "/" + fileName;
            try {
                file.transferTo(Paths.get(path));
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("path", path);
                map.put("city", form.getCity());
                map.put("district", form.getDistrict());
                map.put("address", form.getAddress());
                map.put("country", form.getCountry());
                map.put("province", form.getProvince());
                checkinService.checkin(map);
                return R.ok("签到成功");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new EmosException("图片保存错误");
            } finally {
                FileUtil.del(path);
            }
        }
    }

    /**
     * 创建人脸模型
     *
     * @param file  上传的文件
     * @param token 令牌
     * @return R对象
     */
    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传文件");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("必须提交jpg格式图片");
        } else {
            String path = imageFolder + "/" + fileName;
            try {
                file.transferTo(Paths.get(path));
                checkinService.createFaceModel(userId, path);
                return R.ok("签到成功");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new EmosException("图片保存错误");
            } finally {
                FileUtil.del(path);
            }
        }
    }

    /**
     * 查询用户当日签到数据
     *
     * @param token 令牌
     * @return R对象
     */
    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当天签到数据")
    public R searchTodayCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);

        HashMap map = checkinService.searchTodayCheckin(userId);
        map.put("attendanceTime", systemConstants.attendanceTime);
        map.put("closingTime", systemConstants.closingTime);

        long days = checkinService.searchCheckinDays(userId);
        map.put("checkinDays", days);

        //获得入职日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        //获取本周的开始日期
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        //获取本周的结束日期
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());

        //放置查询的参数
        HashMap param = new HashMap<>();
        param.put("startDate", startDate);
        param.put("endDate", endDate.toString());
        param.put("userId", userId);

        ArrayList<HashMap> list = checkinService.searchWeekCheckin(param);

        map.put("weekCheckin", list);

        return R.ok().put("result", map);
    }

    /**
     * 查询用户某月的签到数据
     *
     * @param form  月考勤数据
     * @param token 令牌
     * @return R对象
     */
    @PostMapping("/searchMonthCheckin")
    @ApiOperation("查询用户某月的签到数据")
    public R searchMonthCheckin(@Valid @RequestBody SearchMonthCheckinForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);

        //查询日志日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));

        //把月份处理成双数字
        String month = form.getMonth() < 10 ? "0" + form.getMonth() : form.getMonth().toString();

        //某年某月的起始日期
        DateTime startDate = DateUtil.parse(form.getYear() + "-" + month + "-01");

        //如果查询的日期早于员工入职日期就抛出异常
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) {
            throw new EmosException("只能查询考勤之后日期的数据");
        }

        //如果查询月份与入职月份相同，本月的考勤开始日期设置为入职日期
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }

        //截止日期
        DateTime endDate = DateUtil.endOfMonth(startDate);

        //封城查询条件
        HashMap param = new HashMap<>();
        param.put("userId", userId);
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());

        //查询到的结果
        ArrayList<HashMap> list = checkinService.searchMothCheckin(param);

        //分别记录是否考勤
        int sum_1 = 0, sum_2 = 0, sum_3 = 0;

        //判断查询到的结果
        for (HashMap<String, String> one : list) {
            String type = one.get("type");
            String status = one.get("status");
            if ("工作日".equals(type)) {
                if ("正常".equals(status)) {
                    sum_1++;
                } else if ("迟到".equals(status)) {
                    sum_2++;
                } else if ("缺勤".equals(status)) {
                    sum_3++;
                }
            }
        }

        return R.ok().put("sum_1", sum_1).put("sum_2", sum_2).put("sum_3", sum_3);
    }
}
