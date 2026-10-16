package com.music.nls.business.common.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException{

    private BusinessExceptionEnum e;

    public BusinessException(BusinessExceptionEnum e) {
        super(e.getDesc());
        this.e = e;
    }

    /**
     * 不写入堆栈信息，简化异常日志，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }//重写 fillInStackTrace方法，该方法用于填充堆栈跟踪信息。返回 this，意味着不写入堆栈信息，从而简化异常日志并提高性能。
}
