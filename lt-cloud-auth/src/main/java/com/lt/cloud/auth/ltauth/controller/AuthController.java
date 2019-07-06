package com.lt.cloud.auth.ltauth.controller;

import com.lt.cloud.auth.ltauth.moudle.dto.LoginDto;
import com.lt.cloud.auth.ltauth.service.AuthService;
import com.lt.cloud.common.ResultMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/user")
public class AuthController {
    @Autowired
    @Lazy
    private AuthService authService;

    /**
     * 登录
     * @param dto
     * @return
     */
    @PostMapping(value = "/login")
    public ResultMsg userLogin(@Valid @RequestBody LoginDto dto){
        try{
            return authService.login(dto);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResultMsg.ERROR(e.getMessage());
        }
    }

    /**
     * token 刷新
     * @param refreshToken
     * @return
     */
    @GetMapping("/token/refresh")
    public ResultMsg refreshToken(String refreshToken){
        try{
            return authService.refreshToken(refreshToken);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResultMsg.ERROR(e.getMessage());
        }
    }

    /**
     * token 验证
     * @param token token
     * @return
     */
    @PostMapping("/token/verify")
    public ResultMsg verifyToken(String token){
        try{
            return authService.verifyToken(token);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResultMsg.ERROR(e.getMessage());
        }
    }
}
