package com.itheima.exception;

import com.itheima.pojo.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    //处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity businessExceptionHandler(BusinessException be){
        be.printStackTrace();
        ErrorResult errorResult = be.getErrorResult();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }
    //处理不可预知的异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception ex){
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
    }
}
