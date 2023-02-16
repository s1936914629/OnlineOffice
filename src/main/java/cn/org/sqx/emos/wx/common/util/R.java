package cn.org.sqx.emos.wx.common.util;

import cn.hutool.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回对象
 *
 * @auther: sqx
 * @Date: 2022-11-12
 */
public class R extends HashMap<String, Object> {
    public R() {
        put("code", HttpStatus.HTTP_OK);
        put("msg", "success");
    }

    //创建成功的静态工厂
    public static R ok() {
        return new R();
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    //创建失败的静态工厂
    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R error(String msg) {
        return error(HttpStatus.HTTP_INTERNAL_ERROR, msg);
    }

    public static R error(int code) {
        return error(code, "未知异常，请联系管理员");
    }

    //重写put方法，方便链式调用
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }


}
