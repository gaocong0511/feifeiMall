package com.fMall.service;

import com.fMall.common.ServerResponse;
import com.fMall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

/**
 * Created by 高琮 on 2018/5/24.
 */
public interface IShippingService {
    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse del(Integer userId,Integer shippingId);

    ServerResponse update(Integer userId,Shipping shipping);

    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize);
}
