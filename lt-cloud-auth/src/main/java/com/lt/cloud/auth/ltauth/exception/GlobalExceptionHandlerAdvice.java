package com.lt.cloud.auth.ltauth.exception;

import com.lt.cloud.common.ResultMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    /**
     *  校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultMsg validationBodyException(MethodArgumentNotValidException exception){

        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p ->{
                FieldError fieldError = (FieldError) p;
                logger.error("Data check failure : object{"+fieldError.getObjectName()+"},field{"+fieldError.getField()+
                             "},errorMessage{"+fieldError.getDefaultMessage()+"}");
            });
        }
        return ResultMsg.ERROR("请填写参数正确信息");
    }

    /**
     * 全局捕获IOException
     * @param exception
     * @return
     */
    @ExceptionHandler(IOException.class)
    public ResultMsg ioErrorHandler(IOException exception){
        logger.error(exception.getMessage(),exception);
        return ResultMsg.ERROR(exception.getMessage());
    }
}
