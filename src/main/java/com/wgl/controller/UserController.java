package com.wgl.controller;

import com.alibaba.druid.util.StringUtils;
import com.wgl.error.BusinessException;
import com.wgl.error.EmBusinessError;
import com.wgl.model.UserModel;
import com.wgl.response.CommonReturnType;
import com.wgl.service.UserService;
import com.wgl.viewobject.UserVO;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController {
    @Autowired
    UserService userService;
    @Autowired
    HttpServletRequest httpServletRequest;


    //用户登录接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
//    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone")String telephone,
                                  @RequestParam(name = "password" )String password) throws Exception {
        //入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                                        StringUtils.isEmpty(password)){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //调试加密···密码
//        System.out.println(this.EncodeByMD5(password));
        //
        UserModel userModel = userService.validateLogin(telephone, this.EncodeByMD5(password));

        httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        httpServletRequest.getSession().setAttribute("USER",userModel);
        return CommonReturnType.create(null);
    }
    //用户注册接口
//    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone")String telephone,
//                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name="password")String password) throws Exception {
        //验证手机号和对应的opt相结合
//        String sessionOptCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);
//        if (!StringUtils.equals(sessionOptCode, otpCode)) {
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合要求");
//        }

        //用户注册
        UserModel model = new UserModel();
        model.setTelephone(telephone);
        model.setAge(age);
        model.setGender(gender);
        model.setName(name);
        model.setRegisterMode("byphone");

        model.setEncrptPassword(EncodeByMD5(password));

        userService.register(model);
        return CommonReturnType.create(null);
    }

    //加密密码
    private String EncodeByMD5(String str) throws Exception {
        MessageDigest md5= MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder=new BASE64Encoder();
        return base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
    }

    //用户获取otp的短信接口
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone) {
        //按照一定的规则生成otp验证码
        Random random = new Random();
        int nextInt = random.nextInt(99999);

        nextInt += 10000;
        String optCode = String.valueOf(nextInt);


        //将otp验证码对应用户的手机号关联,当在使用分布式开发时，我们采用redis的方式进行存储，在这里我们采用httpsession的方式绑定他的手机号与optCode
        httpServletRequest.getSession().setAttribute(telephone,optCode);


        //将otp验证码通过短信发送给用户

        System.out.println("telephone = " + telephone + "& otpCode =" + optCode);
        return CommonReturnType.create(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);

        //若获取的用户信息不存在
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);

        }

        //调用service服务获取对应id的用户对象并返回给前端
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    public UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }

}
