package com.wgl.service;

import com.wgl.error.BusinessException;
import com.wgl.model.UserModel;

public interface UserService {
     UserModel getUserById(Integer id);
     void register(UserModel userModel) throws BusinessException;
     UserModel validateLogin(String telephone ,String encrptPassword) throws BusinessException;
}
