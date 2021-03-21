package com.kakaopay.codingTest.common.exception;

/**
 * InvalidValueException 정의
 */
public class InvalidValueException extends BusinessException {

    public InvalidValueException() { super(ErrorCode.INVALID_INPUT_VALUE); }

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
