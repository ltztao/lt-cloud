package com.lt.cloud.auth.ltauth.moudle.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class LoginDto implements Serializable {

    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "用户密码不能为空")
    private String password;
}
