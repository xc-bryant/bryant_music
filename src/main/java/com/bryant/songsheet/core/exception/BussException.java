package com.bryant.songsheet.core.exception;

/**
 * @author bryant
 * @date 2024/7/8
 **/
public class BussException extends RuntimeException{
    public BussException() {
    }

    public BussException(String message) {
        super(message);
    }

    public BussException(Throwable cause) {
        super(cause);
    }

    public BussException(String message, Throwable cause) {
        super(message, cause);
    }
}
