package com.clash.component;

import com.clash.IComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ComponentContainer {
    private Map<Class<? extends IComponent>, IComponent> components = new HashMap<>();

    public <T extends IComponent>void register(T component) {
        components.put(component.getClass(), component);
    }

    @SuppressWarnings("unchecked")
    public <T extends IComponent> T getComponent(Class<T> clazz) {
        return (T) components.get(clazz);
    }

    public <T extends IComponent> T getComponentOrElsePut(Class<T> clazz, Supplier<T> supplier) {
        T component = getComponent(clazz);
        if(null == component) {
            component = supplier.get();
            register(component);
        }
        return component;
    }

    public Collection<IComponent> getComponents() {
        return components.values();
    }

}
