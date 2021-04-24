package com.wgl.service.Impl;

import com.wgl.dao.OrderDOMapper;
import com.wgl.dao.SequenceDOMapper;
import com.wgl.dataobject.OrderDO;
import com.wgl.dataobject.SequenceDO;
import com.wgl.error.BusinessException;
import com.wgl.error.CommonError;
import com.wgl.error.EmBusinessError;
import com.wgl.model.ItemModel;
import com.wgl.model.OrderModel;
import com.wgl.model.UserModel;
import com.wgl.service.ItemService;
import com.wgl.service.OrderService;
import com.wgl.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        //1.商品校验，判断商品是否合法
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不正确");
        }
        //2.落单减库存或者支付减库存
        boolean b = itemService.decreaseStock(itemId, amount);
        if (!b) {
            throw new BusinessException(EmBusinessError.ORDER_NOT_ENOUGH);
        }
        //校验商品是否存在活动信息
        if (promoId != null) {
            if (promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BusinessException(EmBusinessError.STOCK_NOT_EQUAL);
            } else if (itemModel.getPromoModel().getStatus() != 2) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动还未开始");
        }
    }

    //3，商品入库
    OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId!=null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

    //获取交易的流水号顶到号
    String orderNo = generateOrderNo();
        orderModel.setId(orderNo);
    OrderDO orderDO = this.convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        itemService.increaseSales(itemId,amount);
    //4，返回前端
        return orderModel;
}

    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) return null;
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }

    //获取唯一的时间序列
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo() {
        //订单号为16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);
        //中间6位为自增序列
        int sequence = 0;
        //获取当前Sequence
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        //获得当前的值
        sequence = sequenceDO.getCurrentValue();
        //设置下一次的值
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        //保存到数据库
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        //转成换String，用于拼接
        String seqStr = String.valueOf(sequence);
        //不足的位数，用0填充
        for (int i = 0; i < 6 - seqStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(seqStr);
        //最后2位为分库分表为，暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }

//    @Override
//    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
//        return null;
//    }
}
