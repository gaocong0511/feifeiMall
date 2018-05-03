package com.fMall.service.impl;

import com.fMall.common.Const;
import com.fMall.common.ServerResponse;
import com.fMall.common.TokenCache;
import com.fMall.dao.UserMapper;
import com.fMall.pojo.User;
import com.fMall.service.IUserService;
import com.fMall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by 高琮 on 2018/5/1.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登陆
     *
     * @param username 用户名
     * @param password 密码
     * @return 统一返回对象
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        //校验用户名
        ServerResponse response = checkValid(username, Const.USERNAME);
        if (!response.isSuccess()) {
            return response;
        }

        //todo 密码登陆MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessMessage("登陆成功", user);
    }

    /**
     * 用户注册
     *
     * @param user 用户对象
     * @return 统一返回对象
     */
    @Override
    public ServerResponse<String> register(User user) {

        //校验用户名
        ServerResponse response = checkValid(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) {
            return response;
        }
        //校验email
        response = checkValid(user.getEmail(), user.getEmail());
        if (!response.isSuccess()) {
            return response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //密码MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 校验用户输入信息是不是正确
     *
     * @param str  用户输入的数据
     * @param type 用户输入的数据类型
     * @return 统一返回对象
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //校验用户名
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已经被占用");
                }
            }
            //校验email
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("该邮箱已经被注册");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 获得用户的找回密码的问题
     *
     * @param username 用户名
     * @return 统一返回对象string型
     */
    @Override
    public ServerResponse<String> getQuestion(String username) {
        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        //说明用户名当前还没有被占用
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestions(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    /**
     * 验证用户找回密码问题答案是不是正确
     *
     * @param username 用户名
     * @param question 问题
     * @param answer   答案
     * @return 统一返回对象 String型
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //用uuid生成一个forgetToken并且设置其有效期
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccessMessage(forgetToken);
        }
        return ServerResponse.createByErrorMessage("输入答案错误");
    }

    /**
     * 用户重置密码
     *
     * @param username    用户名
     * @param password    密码
     * @param forgetToken forgetToken
     * @return 统一返回对象 String型
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，需要传递token");
        }
        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        //说明用户名当前还没有被占用
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者已经过期");
        }
        if (StringUtils.equals(token, forgetToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.forgetResetPassword(username, md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token无效,请重试");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 用户在已经登录了的情况下 重置密码
     * @param session session对象
     * @param passwordOld 旧的密码
     * @param passwordNew 新的密码
     * @return 统一返回对象 String 型
     */
    @Override
    public ServerResponse<String> resetPassWord(HttpSession session, String passwordOld, String passwordNew) {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //验证旧密码，验证现在这个要修改密码的用户就是这个用户
        int resultCount=userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount= userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }else{
            return ServerResponse.createByErrorMessage("修改密码失败");
        }
    }

    /**
     * 更新用户的信息
     * @param session session对象
     * @param user User对象
     * @return 统一返回对象 String型
     */
    @Override
    public ServerResponse<User> updateUserInformation(HttpSession session, User user) {
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        int resultCount=userMapper.checkEmailIsUsed(currentUser.getId(),user.getEmail());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("该邮箱已经被使用，请使用其他邮箱");
        }
        User updateUser=new User();
        updateUser.setId(currentUser.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            updateUser.setUsername(currentUser.getUsername());
            return ServerResponse.createBySuccessMessage("更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }
}
