package com.clash.param;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class HashBasedParam implements IParam {
    private Map<String, Object> kvs = new HashMap<>();

    public static HashBasedParam create(Object obj) {
        HashBasedParam param = new HashBasedParam();
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object val = field.get(obj);
                param.put(field.getName(), val);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return param;
    }

    @Override
    public void put(String key, Object obj) {
        kvs.put(key, obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) kvs.get(key);
    }

    @Override
    public <T> T toObject(Class<T> clazz) {
        try {
            T instance = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                String name = field.getName();
                Object val = kvs.get(name);
                if(null != val) {
                    field.setAccessible(true);
                    field.set(instance, val);
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
