package com.fMall.service;

import com.fMall.common.ServerResponse;

import java.util.Map;

/**
 * Created by 高琮 on 2018/6/11.
 */
public interface IOrderService {
    ServerResponse pay(Long orderId,Integer userId,String path);

    ServerResponse aliCallBack(Map<String,String> params);

    ServerResponse queryOrderPayStatus(Integer userId,long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancel(Integer userId,long orderNo);
}
