package com.lt.cloud.gateway.feign.impl;

import com.lt.cloud.common.ResultMsg;
import com.lt.cloud.gateway.feign.AuthFeign;

public class AuthFeignImpl implements AuthFeign {
    public ResultMsg verifyToken(String token){
        return ResultMsg.ERROR("验证TOKEN服务熔断！");
    }
}
