package com.fMall.service;

import com.fMall.common.ServerResponse;

/**
 * Created by 高琮 on 2018/6/11.
 */
public interface IOrderService {
    ServerResponse pay(Long orderId,Integer userId,String path);
}
