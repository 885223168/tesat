package com.sunzd.crm.settings.service;

import com.sunzd.crm.settings.domain.User;
import com.sunzd.crm.settings.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * SZD
 * 2020/12/23
 * 16:18
 */
public interface UserService {

    // 根据用户名和密码查询用户
    User queryUserByLoginActAndPwd(Map<String,Object> map);

    // 查询所有用户
    List<User> selectAllUsers();
}
