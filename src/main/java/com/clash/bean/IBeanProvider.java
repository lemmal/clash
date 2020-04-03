package com.clash.bean;

public interface IBeanProvider<T> {
    T provide();

    default String name() {
        return "";
    }
}
