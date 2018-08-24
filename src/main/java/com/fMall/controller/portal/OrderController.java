package com.fMall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;
import com.fMall.service.IOrderService;
import com.google.common.collect.Maps;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 高琮 on 2018/6/11.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private final IOrderService iOrderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(IOrderService iOrderService) {
        this.iOrderService = iOrderService;
    }


    /**
     * 新创建一个订单
     *
     * @param session 当前的会话信息
     * @param shippingId 收货地址Id
     * @return  统一返回对象
     */
    @RequestMapping(value = "create.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }


    /**
     * 取消订单
     *
     * @param session 当前的会话信息
     * @param orderNo 订单编号
     * @return 统一返回对象
     */
    @RequestMapping(value = "cancel.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }


    /**
     * 获取订单的购物车列表
     *
     * @param session HTTPSession对象
     * @return 统一返回对象
     */
    @RequestMapping(value = "getOrderCartProduct.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 用户查看自己的订单列表
     *
     * @param session  HTTPSession对象
     * @param pageNum  要查询的页数
     * @param pageSize 要查询的页容量
     * @return 统一返回对象
     */
    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse detail(HttpSession session,Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    /**
     * 支付入口
     *
     * @param session     HTTPSession对象
     * @param orderNumber 订单号
     * @return 统一返回对象
     */
    @RequestMapping(value = "pay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNumber, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNumber, user.getId(), path);
    }


    @RequestMapping(value = "ali_pay_callback.do", method = RequestMethod.POST)
    @ResponseBody
    /**
     * 支付宝回调的入口
     */
    public Object alipayCallback(HttpServletRequest request) {
        Map requestParams = request.getParameterMap();
        Map<String, String> params = Maps.newHashMap();
        for (Iterator iterator = requestParams.keySet().iterator(); iterator.hasNext(); ) {
            String name = (String) iterator.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0, length = values.length; i < length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("成功获取到支付宝回调信息中的参数sign:{},trade_status,参数:{},", params.get("sign"),
                params.get("trade_status"), params.toString());
        //验证回调的正确性  1.来自支付宝 2.并不是重复的通知
        params.remove("sign_type");
        try {
            boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckV2) {
                return ServerResponse.createByErrorMessage("错误的请求参数");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调验证异常", e);
        }
        ServerResponse serverResponse = iOrderService.aliCallBack(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallBack.RESPONSE_SUCCESS;
        } else {
            return Const.AlipayCallBack.RESPONSE_FAILED;
        }

    }

    /**
     * 支付入口
     *
     * @param session     HTTPSession对象
     * @param orderNumber 订单号
     * @return 统一返回对象
     */
    @RequestMapping(value = "query_order_pay_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse queryOrderPayStatus(HttpSession session, Long orderNumber, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = iOrderService.queryOrderPayStatus(user.getId(), orderNumber);
        if (response.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
