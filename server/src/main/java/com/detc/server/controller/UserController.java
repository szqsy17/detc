package com.detc.server.controller;

import com.detc.server.annotation.NeedRoleOne;
import com.detc.server.annotation.SkipVerify;
import com.detc.server.model.Code;
import com.detc.server.model.LoginUser;
import com.detc.server.model.Result;
import com.detc.server.model.UserModel;
import com.detc.server.service.UserService;
import com.detc.server.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author szqsy17
 */
@Log4j2
@Validated
@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=utf-8")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private Result<?> res;

    @SkipVerify
    @PostMapping(value = "/login")
    public String userLogin(String username, String password) {
        res = userService.verifyPwd(username, password);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", res.getData());
    }

    @NeedRoleOne
    @PostMapping(value = "/addOneUser")
    public String addOneUser(UserModel user) {
        res = userService.addOneUser(user);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "添加成功");
    }

    @NeedRoleOne
    @PostMapping(value = "/deleteOneUser")
    public String deleteOneUser(String id) {
        res = userService.deleteOneUser(id);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "删除成功");
    }

    @PostMapping(value = "/updateOneUser")
    public String updateOneUser(UserModel user) {
        res = userService.updateOneUser(user);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "更新");
    }

    @NeedRoleOne
    @GetMapping(value = "/getAllUser")
    public String getAllUser() {
        res = userService.getAllUser();

        return HttpUtils.setMessage("200", res.getData());
    }

    @GetMapping(value = "/getUserSelf")
    public String getUserSelf(LoginUser loginUser) {
        res = userService.getUserSelf(loginUser.getId());

        return HttpUtils.setMessage("200", res.getData());
    }

}
