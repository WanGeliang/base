package com.wgl.controller;

import com.wgl.error.BusinessException;
import com.wgl.error.EmBusinessError;
import com.wgl.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class BaseController {

    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";
    //定义exceptionhandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        HashMap<String, Object> responseMap = new HashMap<>();
        if (ex instanceof BusinessException){
            BusinessException businessException = (BusinessException) ex;
            responseMap.put("errCode",businessException.getErrCode());
            responseMap.put("errMsg",businessException.getErrMsg());
            //打印堆栈信息，开发过程需要。发布后不需要，我们自己开发的时候需要进行
            ex.printStackTrace();
        }else {
            responseMap.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseMap.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
            ex.printStackTrace();
        }

        return CommonReturnType.create(responseMap,"fail");
    }
}
