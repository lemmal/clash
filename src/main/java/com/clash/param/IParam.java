package com.clash.param;

public interface IParam {
    void put(String key, Object obj);

    <T> T get(String key, Class<T> clazz);

    <T> T toObject(Class<T> clazz);
}
