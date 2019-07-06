package com.lt.cloud.gateway.controller;

import com.lt.cloud.common.ResultMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HystrixController {

    @RequestMapping("/authfallback")
    public ResultMsg authFallback(){
        log.error("auth 服务熔断");
        return ResultMsg.HYSTRIX("auth service hystrix!");
    }

    @RequestMapping("/basefallback")
    public ResultMsg baseFallback(){
        log.error("base 服务熔断");
        return ResultMsg.HYSTRIX("base service hystrix!");
    }
}
