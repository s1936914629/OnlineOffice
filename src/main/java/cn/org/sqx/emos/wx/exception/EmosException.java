package cn.org.sqx.emos.wx.exception;

import lombok.Data;

/**
 * @auther: sqx
 * @Date: 2022-11-12
 */
@Data
public class EmosException extends RuntimeException{
    private String msg;         //异常信息
    private int code = 500;     //异常状态码

    //构建异常构造器
    public EmosException(String msg){
        super(msg);
        this.msg = msg;
    }

    public EmosException(String msg,Throwable e){
        super(msg,e);
        this.msg=msg;
    }

    public EmosException(String msg,int code){
        /*
         * 父类只接受参数为String的
         * 父类接受第一个参数为String，第二个参数为Throwable的
         * 父类接受Throwable的
         */
        super(msg);
        this.msg = msg;
        this.code =code;
    }

    public EmosException(String msg,int code,Throwable e){
        super(msg,e);
        this.msg = msg;
        this.code =code;
    }

}


















