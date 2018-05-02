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
    User selectLogin(@Param("username")String username,@Param("password") String password);

    //检查email是不是已经存在
    int checkEmail(String email);

    //查找用户找回密码的问题

    String selectQuestions(String username);
}