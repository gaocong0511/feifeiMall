package com.fMall.controller.portal;

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
 * created by 高琮 on 2018/05/01
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录接口
     *
     * @param username 用户名
     * @param password 密码
     * @param session  session对象
     * @return 统一返回对象
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        //service-->mybatis-->dao层
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 用户登出接口
     *
     * @param session session对象
     * @return 统一返回对象
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册接口
     *
     * @param user User对象
     * @return 统一返回对象
     */
    @RequestMapping(value = "register.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 用户校验接口
     *
     * @param str  用户要校验的数据
     * @param type 要校验数据的类型
     * @return 统一返回对象
     */
    @RequestMapping(value = "checkValid.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取当前登陆用户信息的接口
     *
     * @param session session对象
     * @return 统一返回对象
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取登陆信息");
    }

    /**
     * 当用户忘记密码时，返回当前用户用于找回密码的问题
     *
     * @param username 用户名
     * @return 统一返回对象 string型 用户设置的问题
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.getQuestion(username);
    }

    /**
     * 校验用户忘记密码时的问题是不是正确的
     *
     * @param username 用户名
     * @param question 问题
     * @param answer   密码
     * @return 统一返回对象 String型
     */
    @RequestMapping(value = "check_answer.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 用户忘记密码重置密码
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param forgetToken forgetToken
     * @return 统一返回对象 String型
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登陆状态下的用户修改密码
     *
     * @param session     session对象
     * @param passwordOld 旧的密码
     * @param passwordNew 新的密码
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        return iUserService.resetPassWord(session, passwordOld, passwordNew);
    }

    /**
     * 更新用户的信息
     *
     * @param session session对象
     * @param user    User对象
     * @return 统一返回对象 String型
     */
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        ServerResponse<User> response= iUserService.updateUserInformation(session, user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

}
