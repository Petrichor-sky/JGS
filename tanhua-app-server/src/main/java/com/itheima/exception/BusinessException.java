package com.itheima.exception;

import com.itheima.pojo.ErrorResult;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException{
    private ErrorResult errorResult;

    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}
