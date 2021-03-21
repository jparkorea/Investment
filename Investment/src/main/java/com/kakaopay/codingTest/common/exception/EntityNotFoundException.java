package com.kakaopay.codingTest.common.exception;

/**
 * EntityNotFoundException 정의
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);}
}