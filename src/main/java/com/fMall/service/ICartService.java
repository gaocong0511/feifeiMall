package com.fMall.service;

import com.fMall.common.ServerResponse;
import com.fMall.vo.CartVo;

/**
 * Created by 高琮 on 2018/5/16.
 */
public interface ICartService {
    ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count);
}
