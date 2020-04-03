package com.clash.bean;

import com.clash.Constants;
import com.clash.IManager;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

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
    private Table<Class<?>, String, Object> instances;

    @SuppressWarnings("unchecked")
    public static <T extends IManager> T buildManager(Class<T> clazz, String... paths) throws BeanParseException, BeanConstructException {
        BeanFactory factory = new BeanFactory(Arrays.stream(paths).collect(Collectors.toList()));
        return (T) factory.instances.get(IManager.class, "");
    }

    public BeanFactory(List<String> paths) throws BeanParseException, BeanConstructException {
        LinkedList<String> pathList = new LinkedList<>(paths);
        pathList.add(Constants.DEFAULT_PACKAGE);
        beanParser = new BeanParser().parseClasses(pathList);
        initConsumer();
    }

    private void initConsumer() throws BeanConstructException {
        instances = HashBasedTable.create();
        Set<Class<?>> consumers = beanParser.getConsumers();
        for (Class<?> clazz : consumers) {
            Object instance = createInstance(clazz);
            for (Field field : clazz.getDeclaredFields()) {
                BeanAutowire autowire = field.getAnnotation(BeanAutowire.class);
                if(null == autowire) {
                    continue;
                }
                _setField(instance, field, autowire);
            }
            BeanConstruct construct = clazz.getAnnotation(BeanConstruct.class);
            if(null != construct) {
                instances.put(construct.value(), construct.name(), instance);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<?> clazz) throws BeanConstructException {
        try {
            BeanConstruct construct = clazz.getAnnotation(BeanConstruct.class);
            if(null != construct && instances.contains(construct.value(), construct.name())) {
                return (T) instances.get(construct.value(), construct.name());
            }
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanConstructException(e);
        }
    }

    private <T> void _setField(T instance, Field field, BeanAutowire autowire) throws BeanConstructException {
        try {
            field.setAccessible(true);
            Object tmp = instances.get(field.getType(), autowire.value());
            Object val = null == tmp ? produce(field, autowire) : tmp;
            field.set(instance, val);
        } catch (IllegalAccessException e) {
            throw new BeanConstructException(e);
        }
    }

    private Object produce(Field field, BeanAutowire autowire) throws BeanConstructException {
        Object instance = produceByConstruct(field.getType(), autowire);
        instance = null == instance ? produceByProvide(field.getType(), autowire) : instance;
        if(null == instance) {
            throw new IllegalArgumentException(String.format("produce failed. can not create instance of %s, name: %s", field.getType().getName(), autowire.value()));
        }
        instances.put(field.getType(), autowire.value(), instance);
        return instance;
    }

    private Object produceByConstruct(Class<?> fieldClass, BeanAutowire autowire) throws BeanConstructException {
        Class<?> clazz = beanParser.getConstructs().get(fieldClass, autowire.value());
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

    private Object produceByProvide(Class<?> fieldClass, BeanAutowire autowire) {
        IBeanProvider<?> provider = beanParser.getProviders().get(fieldClass, autowire.value());
        if(null == provider) {
            return null;
        }
        return provider.provide();
    }
}
