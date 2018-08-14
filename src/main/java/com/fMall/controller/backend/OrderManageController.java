package com.fMall.controller.backend;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;
import com.fMall.service.IOrderService;
import com.fMall.service.IUserService;
import com.fMall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 高琮 on 2018/7/24.
 */
@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {
    private final IUserService iUserService;
    private final IOrderService iOrderService;


    public OrderManageController(IUserService iUserService, IOrderService iOrderService) {
        this.iUserService = iUserService;
        this.iOrderService = iOrderService;
    }

    /**
     * 管理员管理订单列表
     *
     * @param session  HTTPSession对象
     * @param pageNum  要显示的页码数
     * @param pageSize 要显示的每页的数量
     * @return 统一返回对象--分页后的结果
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 管理员查看订单的详情信息
     *
     * @param session HTTPSession对象
     * @param orderNo 订单编号
     * @return 统一返回对象 Order的Vo类型
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "管理员未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 管理员搜索订单
     *
     * @param session  HTTPSession对象
     * @param orderNo  订单编号
     * @param pageNum  要显示的页码数
     * @param pageSize 每页显示的容量
     * @return 统一返回对象 已经分好页的信息
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session,
                                                Long orderNo,
                                                @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSerach(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 管理员进行发货操作
     *
     * @param session HTTPSession对象
     * @param orderNo 订单编号
     * @return 统一返回对象  是不是发货成功了
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

        }
        if (iUserService.checkAdminRole(user).isSuccess()) {

            return iOrderService.mangeSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
