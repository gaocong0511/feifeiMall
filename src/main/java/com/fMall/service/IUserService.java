package com.fMall.service;

import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;

/**
 * Created by 高琮 on 2018/5/1.
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> getQuestion(String username);
}
