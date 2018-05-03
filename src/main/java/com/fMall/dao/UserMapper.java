package com.fMall.dao;

import com.fMall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    //xml之中要对应注解之中的名称
    User selectLogin(@Param("username") String username, @Param("password") String password);

    //检查email是不是已经存在
    int checkEmail(String email);

    //查找用户找回密码的问题
    String selectQuestions(String username);

    //验证用户输入的找回密码的答案是不是正确
    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    //忘记密码时重置密码
    int forgetResetPassword(@Param("username") String username,@Param("passwordNew") String password);

    //验证当前用户的密码是不是正确
    int checkPassword(@Param("userId") Integer userId,@Param("password")String password);

    //验证要修改的邮箱是不是已经被使用
    int checkEmailIsUsed(@Param("userId") Integer userId,@Param("email") String email);
}