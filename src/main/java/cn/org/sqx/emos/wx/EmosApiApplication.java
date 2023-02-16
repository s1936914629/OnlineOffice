package cn.org.sqx.emos.wx;

import cn.hutool.core.util.StrUtil;
import cn.org.sqx.emos.wx.config.SystemConstants;
import cn.org.sqx.emos.wx.domain.SysConfig;
import cn.org.sqx.emos.wx.mapper.SysConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @auther: sqx
 * @Date: 2022-11-10
 */

@Slf4j
@EnableAsync
@SpringBootApplication
@ServletComponentScan   //开启拦截
@MapperScan("cn.org.sqx.emos.wx.mapper")
public class EmosApiApplication {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private SystemConstants constants;

    @Value("${emos.image-folder}")
    private String imageFolder;

    public static void main(String[] args) {
        SpringApplication.run(EmosApiApplication.class, args);
    }

    // 初始化获得常量
    @PostConstruct      //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。
    public void init(){
        List<SysConfig> list = sysConfigMapper.selectAllParam();
        list.forEach(one ->{
            String paramKey = one.getParamKey();
            paramKey = StrUtil.toCamelCase(paramKey); //下划线命名转为驼峰命名
            String paramValue = one.getParamValue();
            try{
                //用反射方法给常量赋值
                Field field = constants.getClass().getDeclaredField(paramKey);
                field.set(constants,paramValue);
            }catch (Exception e){
                log.error("执行异常",e);
            }

        });
        new File(imageFolder).mkdirs();
    }
}
