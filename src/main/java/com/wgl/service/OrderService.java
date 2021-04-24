package com.wgl.service;

import com.wgl.error.BusinessException;
import com.wgl.model.OrderModel;
import org.omg.PortableInterceptor.INACTIVE;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException;
}
