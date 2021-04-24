package com.wgl.error;

public enum EmBusinessError implements CommonError {
    //通用的错误类型00001
    PARAMETER_VALIDATION_ERROR(10001,"输入的参数不合法"),

    //未知错误
    UNKNOWN_ERROR(10002,"未知错误"),
    //10000开头的为用户信息相关的错误信息
    USER_NOT_EXIST(20001,"用户不存在"),
    //用户名或者密码错误
    USER_FAIL(20002,"登录的用户名或者密码错误"),

    //以30000开头的为交易信息错误
    ORDER_NOT_ENOUGH(30000,"库存商品数量不足"),
    //交易时，用户没有进行登录
    USER_NOT_LOGIN(30001,"用户没有登陆，不能进行下单"),
    STOCK_NOT_EQUAL(30002,"商品信息与秒杀信息不符合");

    ;

    private int errCode;
    private String errMsg;

    private EmBusinessError(int errCode,String errMsg){
        this.errCode=errCode;
        this.errMsg=errMsg;
    }
    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg=errMsg;
        return this;
    }
}
