package com.lt.cloud.auth.ltauth.service;

import com.lt.cloud.auth.ltauth.moudle.dto.LoginDto;
import com.lt.cloud.common.ResultMsg;

public interface AuthService {

  ResultMsg login(LoginDto dto);
  ResultMsg refreshToken(String refreshToken);
  ResultMsg verifyToken(String token);

}
