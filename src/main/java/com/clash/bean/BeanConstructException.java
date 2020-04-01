package com.clash.bean;

/**
 * 获取实例失败
 */
public class BeanConstructException extends Exception {

    public BeanConstructException(String message) {
        super(message);
    }

    public BeanConstructException(Throwable cause) {
        super(cause);
    }
}
