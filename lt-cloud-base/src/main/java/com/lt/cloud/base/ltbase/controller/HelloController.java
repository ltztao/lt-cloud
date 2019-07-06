package com.lt.cloud.base.ltbase.controller;

import com.lt.cloud.common.ResultMsg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class HelloController {

    @GetMapping(value = "/hello")
    public ResultMsg testHello(){
        return ResultMsg.SUCCESS("hello word!");
    }

}
