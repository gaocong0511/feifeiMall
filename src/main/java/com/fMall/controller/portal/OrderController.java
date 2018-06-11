package com.fMall.controller.portal;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by 高琮 on 2018/6/11.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    /**
     * 支付入口
     * @param session HTTPSession对象
     * @param orderNumber 订单号
     * @return 统一返回对象
     */
    public ServerResponse pay(HttpSession session,Long orderNumber,HttpServletRequest request){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path=request.getSession().getServletContext().getRealPath("upload");
return null;
    }
}
