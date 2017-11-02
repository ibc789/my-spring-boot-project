package io.renren.modules.api.controller;


import com.alibaba.fastjson.JSON;
import io.renren.common.controller.BaseController;
import io.renren.common.utils.Result;
import io.renren.common.validator.Assert;
import io.renren.modules.api.annotation.AuthIgnore;
import io.renren.modules.api.config.JWTConfig;
import io.renren.modules.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * API登录授权
 *
 * @author ChunLiang Hu
 * @email davichi2009@gmail.com
 * @date 2017-03-23 15:31
 */
@Slf4j
@RestController
//@RequestMapping("/api")
@Api("登录接口")
public class ApiLoginController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTConfig jwtConfig;
    @Autowired
    private Router router;

    @AuthIgnore
    @ApiOperation(value = "登录", notes = "登录说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "mobile", value = "手机号", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "password", value = "密码", required = true)
    })
    public void login(RoutingContext routingContext) {
        MultiMap params = routingContext.request().params();

        String mobile = params.get("mobile");
        String password = params.get("password");
        Assert.isBlank(mobile, "手机号不能为空");
        Assert.isBlank(password, "密码不能为空");

        //用户登录
        long userId = userService.login(mobile, password);

        //生成token
        String token = jwtConfig.generateToken(userId);

        Map<String, Object> map = new HashMap<>(16);
        map.put("token", token);
        map.put("expire", jwtConfig.getExpire());
        doSuccess(routingContext, JSON.toJSONString(Result.ok(map)));
    }

    @Override
    @PostConstruct
    protected void deploy() {
        this.router.get("/api/login").handler(this::login);
    }
}
