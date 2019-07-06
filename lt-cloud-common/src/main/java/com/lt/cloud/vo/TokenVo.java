package com.lt.cloud.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenVo implements Serializable {
    private String token;
    private String refreshToken;

    public TokenVo(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
