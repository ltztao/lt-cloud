package com.lt.cloud.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.lt.cloud.common.ResultMsg;
import com.lt.cloud.gateway.feign.AuthFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${auth.skip.urls}")
    private String[] skipAuthUrls;

    @Autowired
    private AuthFeign authFeign;


    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        //跳过不需要验证的路径
        if(Arrays.asList(skipAuthUrls).contains(url)){
            return chain.filter(exchange);
        }
        //从请求头中取出token
        String token = exchange.getRequest().getHeaders().getFirst("auth-token");
        if (StringUtils.isBlank(token)) {
            return error(exchange,JSONObject.toJSONString(ResultMsg.InvalidToken));
        }
        ResultMsg verifyMsg = null;
        try{
            verifyMsg = authFeign.verifyToken(token);
        }catch (Exception e){
            log.error("验证服务token异常{}",e.getMessage());
            return error(exchange,JSONObject.toJSONString(ResultMsg.HYSTRIX("验证服务token异常")));
        }
        if (verifyMsg.getCode() != 0) {
            return error(exchange,JSONObject.toJSONString(verifyMsg));
        }
        //将现在的request，添加当前身份
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("Authorization-UserName", String.valueOf(verifyMsg.getData())).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);
    }

    private Mono<Void> error(ServerWebExchange exchange,String data){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

}