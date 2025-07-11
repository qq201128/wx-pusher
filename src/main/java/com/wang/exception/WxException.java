package com.wang.exception;

/**
 *  基本异常类
 */
public class WxException extends RuntimeException {
    private String errorCode = "-1";

    private String errorMessage = "";

    public WxException() {
        super();
    }

    public WxException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public WxException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public WxException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
