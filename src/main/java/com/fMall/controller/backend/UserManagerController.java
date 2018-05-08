package com.fMall.controller.backend;

import com.fMall.common.Const;
import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;
import com.fMall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 高琮 on 2018/5/4.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {

    private final IUserService iUserService;

    @Autowired
    public UserManagerController(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @param session  HttpSession对象
     * @return 统一返回对象 user型
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = (User) response.getData();
            //判断当前登录的是不是管理员
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            }else{
                return ServerResponse.createByErrorMessage("当前用户不是管理员，不能进行管理员登录");
            }
        }
        return response;
    }
}
