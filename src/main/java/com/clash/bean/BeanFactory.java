package com.clash.bean;

import com.clash.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class BeanFactory {
    private Map<Class<?>, Constructor<?>> functions;

    public BeanFactory(String... paths) throws BeanParseException {
        String[] p = Stream.concat(Stream.of(Constants.DEFAULT_PATH), Arrays.stream(paths)).toArray(String[]::new);
        functions = BeanParser.INSTANCE.parse(p);
    }

    @SuppressWarnings("unchecked")
    public <T> T produce(Field field) throws BeanConstructException {
        Constructor<T> constructor = (Constructor<T>) functions.get(field.getType());
        constructor.setAccessible(true);
        return _produce(constructor);
    }

    private <T> T _produce(Constructor<T> constructor) throws BeanConstructException {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanConstructException(e);
        }
    }

}
