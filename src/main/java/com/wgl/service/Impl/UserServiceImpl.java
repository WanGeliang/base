package com.wgl.service.Impl;

import com.wgl.dao.UserDOMapper;
import com.wgl.dao.UserPasswordDOMapper;
import com.wgl.dataobject.UserDO;
import com.wgl.dataobject.UserPasswordDO;
import com.wgl.error.BusinessException;
import com.wgl.error.EmBusinessError;
import com.wgl.model.UserModel;
import com.wgl.service.UserService;
import com.wgl.validator.ValidationResult;
import com.wgl.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDOMapper userDOMapper;
    @Autowired
    UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO, userPasswordDO);
    }

    //用户注册功能
    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if (StringUtils.isEmpty(userModel.getName())||
//                userModel.getGender()==null||
//                userModel.getAge()==null||
//                StringUtils.isEmpty(userModel.getTelephone())){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }


        UserDO userDO = convertFromDataObject(userModel);
        userDOMapper.insertSelective(userDO);
        System.out.println(userDO.getId());
        //得到主键
//        userModel.setId(userDO.getId());
        ///调试
        System.out.println(userModel.getId());
        ///
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);

        userPasswordDOMapper.insertSelective(userPasswordDO);


    }

    //校验用户登录是否合法
    @Override
    public UserModel validateLogin(String telephone, String encrptPassword) throws BusinessException {
        //先通过手机号拿到用户信息
        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        if (userDO == null) {
            throw new BusinessException(EmBusinessError.USER_FAIL);
        }
//        System.out.println(userDO.getId());
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        //
//        System.out.println(userPasswordDO);
        //
        //将userdo转化为usermodel
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);
        if (!encrptPassword.equals(userModel.getEncrptPassword())) {
            throw new BusinessException(EmBusinessError.USER_FAIL);
        }
        return userModel;

    }

    public UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());

        UserDO userDO = userDOMapper.selectByTelephone(userModel.getTelephone());

        userPasswordDO.setUserId(userDO.getId());
//        System.out.println(userPasswordDO.getId()+"---"+userPasswordDO.getEncrptPassword()+"---"+userPasswordDO.getUserId());
        return userPasswordDO;
    }

    public UserDO convertFromDataObject(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    public UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);
        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
