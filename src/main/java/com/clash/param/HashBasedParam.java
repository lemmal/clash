package com.clash.param;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class HashBasedParam {

    public static CommandParam createWithCommand(Object object, String command) {
        CommandParam param = new CommandParam();
        param.param = create(object);
        param.command = command;
        return param;
    }

    public static Param create(Object obj) {
        Param param = new Param();
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

    public static class Param implements IParam {
        private Map<String, Object> kvs = new HashMap<>();

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

    public static class CommandParam implements ICommandParam{
        private String command;
        private Param param;

        @Override
        public String getCommand() {
            return command;
        }

        @Override
        public void put(String key, Object obj) {
            param.put(key, obj);
        }

        @Override
        public <T> T get(String key, Class<T> clazz) {
            return param.get(key, clazz);
        }

        @Override
        public <T> T toObject(Class<T> clazz) {
            return param.toObject(clazz);
        }
    }
}
