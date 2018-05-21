package com.fMall.service.impl;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.dao.CartMapper;
import com.fMall.pojo.Cart;
import com.fMall.service.ICartService;
import com.fMall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.rmi.ServerError;

/**
 * Created by 高琮 on 2018/05/19
 */
public class CartServiceImpl implements ICartService {

    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    /**
     * 向购物车之中增加商品
     *
     * @param userId    用户ID
     * @param productId 商品的ID
     * @param count     商品的数量
     * @return Cart的Vo对象
     */
    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if(productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }

        Cart cart=cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart==null){
            //当前这个产品不在这个用户的购物车之中
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }
        return null;
    }
}
