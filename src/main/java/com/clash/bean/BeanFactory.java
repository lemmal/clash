package com.clash.bean;

import com.clash.Constants;
import com.clash.IManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 简单的Bean生成逻辑，一个BeanFactory实例对应的Bean实例仅支持单例
 */
public class BeanFactory {
    private BeanParser beanParser;
    private Map<Class<?>, Object> instances;

    public static IManager buildManager(Class<? extends IManager> clazz, String... paths) throws BeanParseException, BeanConstructException {
        BeanFactory factory = new BeanFactory(Arrays.stream(paths).collect(Collectors.toList()));
        return (IManager) factory.instances.get(clazz);
    }

    public BeanFactory(List<String> paths) throws BeanParseException, BeanConstructException {
        LinkedList<String> pathList = new LinkedList<>(paths);
        pathList.add(Constants.DEFAULT_PATH);
        beanParser = new BeanParser().parseClasses(pathList);
        initConsumer();
    }

    private void initConsumer() throws BeanConstructException {
        instances = new HashMap<>();
        Set<Class<?>> consumers = beanParser.getConsumers();
        for (Class<?> clazz : consumers) {
            Object instance = createInstance(clazz);
            for (Field field : clazz.getDeclaredFields()) {
                BeanAutowire autowire = field.getAnnotation(BeanAutowire.class);
                if(null == autowire) {
                    continue;
                }
                _setField(instance, field);
            }
            instances.put(clazz, instance);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<?> clazz) throws BeanConstructException {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanConstructException(e);
        }
    }

    private <T> void _setField(T instance, Field field) throws BeanConstructException {
        try {
            field.setAccessible(true);
            field.set(instance, produce(field));
        } catch (IllegalAccessException e) {
            throw new BeanConstructException(e);
        }
    }

    private Object produce(Field field) throws BeanConstructException {
        Object instance = produceByConstruct(field.getType());
        instance = null == instance ? produceByProvide(field.getType()) : instance;
        if(null == instance) {
            throw new IllegalArgumentException(String.format("produce failed. can not create instance of %s", field.getType().getName()));
        }
        return instance;
    }

    private Object produceByConstruct(Class<?> fieldClass) throws BeanConstructException {
        Class<?> clazz = beanParser.getConstructs().get(fieldClass);
        if(null == clazz) {
            return null;
        }
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new BeanConstructException(e);
        }
    }

    private Object produceByProvide(Class<?> fieldClass) throws BeanConstructException {
        Class<? extends IBeanProvider<?>> provider = beanParser.getProviders().get(fieldClass);
        if(null == provider) {
            return null;
        }
        try {
            return provider.newInstance().provide();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanConstructException(e);
        }
    }
}
