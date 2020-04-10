package com.clash.handler;

import com.clash.bean.BeanFactory;

import java.util.HashMap;
import java.util.Map;

public class InvokeHandlerParser {

    public static Map<String, IInvokeHandler> parseHandlers(Class<?> clazz, BeanFactory beanFactory) {
        return _parseHandlers(clazz, beanFactory);
    }

    private static Map<String, IInvokeHandler> _parseHandlers(Class<?> clazz, BeanFactory beanFactory){
        Map<String, IInvokeHandler> handlers = new HashMap<>();
        try {
            Class<?>[] classes = clazz.getDeclaredClasses();
            for (Class<?> aClass : classes) {
                if(IInvokeHandler.class.isAssignableFrom(aClass) && aClass.isAnnotationPresent(InvokeCommand.class)) {
                    handlers.put(aClass.getAnnotation(InvokeCommand.class).value(), (IInvokeHandler) beanFactory.consumeBean(aClass, ""));
                }
            }
            return handlers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
