package com.fMall.controller.portal;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;
import com.fMall.service.ICartService;
import com.fMall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 高琮 on 2018/5/19.
 */

@Controller
@RequestMapping("/cart/")
public class CartController {


    private final ICartService iCartService;
    @Autowired
    public CartController(ICartService iCartService) {
        this.iCartService = iCartService;
    }


    @RequestMapping("get_cart_list.do")
    @ResponseBody
    public ServerResponse<CartVo> getProductList(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.getCartList(user.getId());
    }


    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session,Integer productId,Integer count){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }
}
