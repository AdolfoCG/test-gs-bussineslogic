package com.adolfo.test.gs.bussineslogic.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

}