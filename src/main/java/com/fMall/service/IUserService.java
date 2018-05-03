package com.fMall.service;

import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * Created by 高琮 on 2018/5/1.
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> getQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken);

    ServerResponse<String> resetPassWord(HttpSession session,String passwordOld,String passwordNew);

    ServerResponse<User> updateUserInformation(HttpSession session,User user);
}
