package com.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * @hzm
 * 服务端响应对象，将相应的状态码，信息，数据，封装在一起。
 * 各个方法，分别返回三个字段的组合。
 * @JsonIgnore 序列化的时候，该注解标识的“对象”不进行序列化
 * @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
 * 序列化之后，若是某个字段（status、msg、data）
 * 为null（如是没有数据传到前端，Json默认为空，并不显示出来），
 * 则不要这字段
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status,String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public String getMsg(){
        return msg;
    }

    public T getData(){
        return data;
    }

    public static <T> ServerResponse<T> creatBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> creatBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> creatBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> creatBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> creatByErroe(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> creatByErrorMessage(String errormsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errormsg);
    }

    public static <T> ServerResponse<T> creatByErrorCodeMessage(int errorCode, String msg){
        return new ServerResponse<T>(errorCode,msg);
    }

}
