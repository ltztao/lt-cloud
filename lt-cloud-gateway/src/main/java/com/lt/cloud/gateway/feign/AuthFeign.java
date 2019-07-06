package com.lt.cloud.gateway.feign;

import com.lt.cloud.common.ResultMsg;
import com.lt.cloud.gateway.feign.impl.AuthFeignImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "lt-cloud-auth",fallback = AuthFeignImpl.class)
public interface AuthFeign {

    @PostMapping("/user/token/verify")
    ResultMsg verifyToken(@RequestParam("token") String token);
}
