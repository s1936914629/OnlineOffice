package cn.org.sqx.emos.wx.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.org.sqx.emos.wx.config.SystemConstants;
import cn.org.sqx.emos.wx.domain.TbCheckin;
import cn.org.sqx.emos.wx.domain.TbFaceModel;
import cn.org.sqx.emos.wx.exception.EmosException;
import cn.org.sqx.emos.wx.mapper.*;
import cn.org.sqx.emos.wx.service.CheckinService;
import cn.org.sqx.emos.wx.task.EmailTask;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2022-12-26
 */
@Service
@Slf4j
@Scope("prototype")
public class CheckinServiceImpl implements CheckinService {
    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private TbHolidaysMapper holidaysMapper;

    @Autowired
    private TbWorkdayMapper workdayMapper;

    @Autowired
    private TbCheckinMapper checkinMapper;

    @Autowired
    private TbFaceModelMapper faceModelMapper;

    @Autowired
    private TbCityMapper cityMapper;

    @Autowired
    private EmailTask emailTask;

    @Autowired
    private TbUserMapper userMapper;

    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Value("${emos.email.hr}")
    private String hrEmail;

    @Value("${emos.code}")
    private String code;

    /**
     * 用户是否可以签到
     *
     * @param userId 用户ID
     * @param date   当天日期
     * @return 具体业务消息
     */
    @Override
    public String validCanCheckIn(int userId, String date) {
        //这两个布尔值不能同时为真
        boolean bool_1 = holidaysMapper.searchTodayIsHolidays() != null;
        boolean bool_2 = workdayMapper.searchTodayIsWorkday() != null;

        String type = "工作日";

        //判断当天是不是节假日
        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }

        //判断是不是特殊工作日或者节假日
        if (bool_1) {
            type = "节假日";
        } else if (bool_2) {
            type = "工作日";
        }

        if (type.equals("节假日")) {
            return "节假日不需要考勤";
        } else {
            //获取当天的日期
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String end = DateUtil.today() + " " + systemConstants.attendanceTime;
            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);
            if (now.isBefore(attendanceStart)) {
                return "没有到上班考勤开始时间";
            } else if (now.isAfter(attendanceEnd)) {
                return "超过了上班考勤结束时间";
            } else {
                HashMap<Object, Object> map = new HashMap<>();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                //判断是否存在考勤记录
                boolean bool = checkinMapper.haveCheckin(map) != null;
                return bool ? "今天已经考勤了，不用重复考勤" : "可以考勤";
            }

        }

    }

    /**
     * 签到方法
     *
     * @param param 封装好的小程序提交的签到数据
     */
    @Override
    public void checkin(HashMap param) {
        //签到判断
        Date dn = DateUtil.date(); //当前时间
        Date ds = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime); //上班开始时间
        Date dsj = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime); //上班考勤结束时间

        int status = 1; //正常考勤
        if (dn.compareTo(ds) <= 0) {
            status = 1;
        } else if (dn.compareTo(ds) > 0 && dn.compareTo(dsj) <= 0) {
            status = 2; //迟到
        }

        //判断人脸模型
        int userId = (int) param.get("userId");
        String faceModel = faceModelMapper.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("人脸模型不存在");
        } else {
            String path = (String) param.get("path"); //获取图片路径

            //查询风险等级
            int risk = 0;
            //保存签到记录
            String city = (String) param.get("city");
            String district = (String) param.get("district");
            String address = (String) param.get("address");
            String country = (String) param.get("country");
            String province = (String) param.get("province");
            if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
                String code = cityMapper.searchCode(city);
                try {
                    //发送请求
                    String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                    //用jsoup来得到返回的HTML
                    Document document = Jsoup.connect(url).get();
                    //解析返回的HTML
                    Elements elements = document.getElementsByClass("list-content");
                    if (elements.size() > 0) {
                        Element element = elements.get(0);
                        //获得最后一个p标签
                        String result = element.select("p:last-child").text();
                        if ("高风险".equals(result)) {
                            risk = 3;
                            //发送告警邮件
                            HashMap<String, String> map = userMapper.searchNameAndDept(userId);
                            String name = map.get("name");
                            String deptName = map.get("dept_name");
                            deptName = deptName != null ? deptName : "";
                            SimpleMailMessage message = new SimpleMailMessage();
                            message.setTo(hrEmail);
                            message.setSubject("员工" + name + "身处高风险疫情地区警告");
                            message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" + address + "，属于疫情高风险地区，请及时与该员工联系，核实情况！");
                            emailTask.sendAsync(message);

                        } else if ("中风险".equals(result)) {
                            risk = 2;
                        } else if ("低风险".equals(result)) {
                            risk = 1;
                        }
                    }
                } catch (Exception e) {
                    log.error("执行异常", e);
                    throw new EmosException("获取风险等级失败");
                }
            }

            //保存签到记录
            TbCheckin entity = new TbCheckin();
            entity.setUserId(userId);
            entity.setAddress(address);
            entity.setCountry(country);
            entity.setProvince(province);
            entity.setCity(city);
            entity.setDistrict(district);
            entity.setStatus((byte) status);
            entity.setRisk(risk);
            entity.setRisk(risk);
            entity.setDate(DateUtil.today());
            entity.setCreateTime(dn);
            checkinMapper.insert(entity);

            /*
            //发出HTTP请求，让Python识别人脸模型
            HttpRequest request = HttpUtil.createPost(checkinUrl);

            //上传照片
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            //调用Python方法需要code
            request.form("code", code);
            HttpResponse response = request.execute();

            //判断状态码，是否发送成功
            if (response.getStatus() != 200) {
                log.error("人脸识别服务异常");
                throw new EmosException("人脸识别服务异常");
            }


            //获取返回的信息
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("签到无效，非本人签到");
            } else if ("True".equals(body)) {
                //查询风险等级
                int risk = 0;
                //保存签到记录
                String city = (String) param.get("city");
                String district = (String) param.get("district");
                String address = (String) param.get("address");
                String country = (String) param.get("country");
                String province = (String) param.get("province");
                if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
                    String code = cityMapper.searchCode(city);
                    try {
                        //发送请求
                        String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                        //用jsoup来得到返回的HTML
                        Document document = Jsoup.connect(url).get();
                        //解析返回的HTML
                        Elements elements = document.getElementsByClass("list-content");
                        if (elements.size() > 0) {
                            Element element = elements.get(0);
                            //获得最后一个p标签
                            String result = element.select("p:last-child").text();
                            if ("高风险".equals(result)) {
                                risk = 3;
                                //发送告警邮件
                                HashMap<String, String> map = userMapper.searchNameAndDept(userId);
                                String name = map.get("name");
                                String deptName = map.get("dept_name");
                                deptName = deptName != null ? deptName : "";
                                SimpleMailMessage message = new SimpleMailMessage();
                                message.setTo(hrEmail);
                                message.setSubject("员工" + name + "身处高风险疫情地区警告");
                                message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" + address + "，属于疫情高风险地区，请及时与该员工联系，核实情况！");
                                emailTask.sendAsync(message);

                            } else if ("中风险".equals(result)) {
                                risk = 2;
                            } else if ("低风险".equals(result)) {
                                risk = 1;
                            }
                        }
                    } catch (Exception e) {
                        log.error("执行异常", e);
                        throw new EmosException("获取风险等级失败");
                    }
                }
                //保存签到记录
                TbCheckin entity = new TbCheckin();
                entity.setUserId(userId);
                entity.setAddress(address);
                entity.setCountry(country);
                entity.setProvince(province);
                entity.setCity(city);
                entity.setDistrict(district);
                entity.setStatus((byte) status);
                entity.setRisk(risk);
                entity.setDate(DateUtil.today());
                entity.setCreateTime(dn);
                checkinMapper.insert(entity);

             */
        }
    }


    /**
     * 创建人脸模型
     *
     * @param userId 用户Id
     * @param path   图片路径
     */
    @Override
    public void createFaceModel(int userId, String path) {
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo", FileUtil.file(path));
        //调用Python方法需要code
        request.form("code", code);
        HttpResponse response = request.execute();
        String body = response.body();
        if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            throw new EmosException(body);
        } else {
            TbFaceModel model = new TbFaceModel();
            model.setUserId(userId);
            model.setFaceModel(body);
            faceModelMapper.insert(model);
        }
    }

    /**
     * 查询签到的结果
     *
     * @param userId 用户id
     * @return 用户签到信息
     */
    @Override
    public HashMap searchTodayCheckin(int userId) {
        return checkinMapper.searchTodayCheckin(userId);
    }

    /**
     * 查询用户的签到天数
     *
     * @param userId 用户id
     * @return 签到天数
     */
    @Override
    public long searchCheckinDays(int userId) {
        return checkinMapper.searchCheckinDays(userId);
    }

    /**
     * 查询一周内，用户的考勤情况
     *
     * @param param 用户信息
     * @return 考勤情况
     */
    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap param) {
        //查询本周的考勤
        ArrayList<HashMap> checkinList = checkinMapper.searchWeekCheckin(param);
        //查询本周的特殊工作日
        ArrayList<String> workdayList = workdayMapper.searchWorkdayInRange(param);
        //查询本周的特殊节假日
        ArrayList<String> holidayList = holidaysMapper.searchHolidaysInRange(param);

        //获得本周的起始日期
        DateTime startDate = DateUtil.parseDate(param.get("startDate").toString());
        //获得本周的结束日期
        DateTime endDate = DateUtil.parseDate(param.get("endDate").toString());

        //生成本周的7天对象
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);

        //返回值
        ArrayList<HashMap> list = new ArrayList<>();

        //判断工作日的考勤情况
        range.forEach(day -> {
            String date = day.toString("yyyy-MM-dd");
            String type = "工作日";
            if (day.isWeekend()) {
                type = "节假日";
            }
            if (holidayList != null && holidayList.contains(date)) {
                type = "节假日";
            } else if (workdayList != null && workdayList.contains(date)) {
                type = "工作日";
            }

            String status = ""; //这个考勤状态是空字符串，因为有可能还没有到查看的那一天
            if (type.equals("工作日") && DateUtil.compare(day, DateUtil.date()) <= 0) {
                status = "缺勤";
                boolean flag = false; //标志位，判断当天考勤是否结束
                for (HashMap<String, String> map : checkinList) {
                    if (map.containsValue(date)) {
                        status = map.get("status");
                        flag = true;
                        break;
                    }
                }

                DateTime endTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);//下班考勤结束时间
                String toDay = DateUtil.today();
                if (date.equals(toDay) && DateUtil.date().isBefore(endTime) && !flag) {
                    status = "";
                }
            }
            //封装返回值
            HashMap map = new HashMap<>();
            map.put("date", date);
            map.put("status", status);
            map.put("type", type);
            map.put("day", day.dayOfWeekEnum().toChinese("周"));
            list.add(map);
        });


        return list;
    }

    /**
     * 查询月考勤
     * 因为查询需要的参数一样，所以可以调用前面封装的查询一周考勤的方法来查询一个月的考勤
     *
     * @param param 需要查询的对象
     * @return 考勤记录
     */
    @Override
    public ArrayList<HashMap> searchMothCheckin(HashMap param) {
        return this.searchWeekCheckin(param);
    }
}
