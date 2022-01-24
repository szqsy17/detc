package com.detc.server.service;

import com.alibaba.fastjson.JSONObject;
import com.detc.server.mapper.UserMapper;
import com.detc.server.model.Result;
import com.detc.server.model.UserModel;
import com.detc.server.util.JwtUtils;
import com.detc.server.util.ResultUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author szqsy17
 */
@Log4j2
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 校验用户密码
     */
    public Result<?> verifyPwd(String username, String password) {
        UserModel user = userMapper.getUserByUsername(username);

        if (user == null) {
            return ResultUtils.failed("用户不存在");
        }

        if (!password.equals(user.getPassword())) {
            return ResultUtils.failed("密码错误");
        }

        JSONObject token = JwtUtils.createJwt(user.getId(), user.getUsername(), user.getRole());
        return ResultUtils.success(token);
    }

    /**
     * 添加一个用户
     */
    public Result<?> addOneUser(UserModel user) {
        UserModel userModel = userMapper.getUserByUsername(user.getUsername());
        if (userModel != null) {
            return ResultUtils.failed("用户已存在");
        }

        userMapper.addOneUser(user);

        return ResultUtils.success();
    }

    /**
     * 删除一个用户
     */
    public Result<?> deleteOneUser(String id) {
        return userMapper.deleteOneUser(id) == 1
                ? ResultUtils.success()
                : ResultUtils.failed("用户不存在");
    }

    /**
     * 更新一个用户
     */
    public Result<?> updateOneUser(UserModel user) {
        UserModel userModel = userMapper.getUserByUsername(user.getUsername());
        if (userModel != null) {
            return ResultUtils.failed("用户名已被使用");
        }

        return userMapper.updateOnePerson(user) == 1
                ? ResultUtils.success()
                : ResultUtils.failed("用户不存在");
    }

    /**
     * 获得所有用户信息
     */
    public Result<?> getAllUser() {
        return ResultUtils.success(userMapper.getAllUser());
    }

    /**
     * 获得一个用户信息
     */
    public Result<?> getUserSelf(String id) {
        return ResultUtils.success(userMapper.getUserById(id));
    }

}
