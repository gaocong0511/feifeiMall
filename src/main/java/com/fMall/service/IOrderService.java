package com.fMall.service;

import com.fMall.common.ServerResponse;
import com.fMall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import com.mysql.fabric.Server;

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

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize);

    ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo);


    //后台管理员的订单
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo>manageSerach(Long orderNo,int pageNum,int pageSize);

    ServerResponse<String> mangeSendGoods(Long orderNo);
}
