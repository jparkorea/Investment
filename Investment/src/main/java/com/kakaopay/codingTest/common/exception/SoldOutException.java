package com.kakaopay.codingTest.common.exception;

/**
 * SoldOutException 정의
 */
public class SoldOutException extends BusinessException{

    public SoldOutException() { super(ErrorCode.SOLD_OUT); }

    public SoldOutException(ErrorCode errorCode) {
        super(errorCode);
    }

}
