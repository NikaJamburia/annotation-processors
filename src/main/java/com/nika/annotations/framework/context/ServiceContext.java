package com.nika.annotations.framework.context;

import com.nika.annotations.framework.annotation.Lazy;
import com.nika.annotations.framework.annotation.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.capitalize;

public class ServiceContext {
    private Map<String, Object> services = new HashMap<>();

    public ServiceContext(String... names) {
        for (String name : names) {
            loadService(name);
        }
        injectServices();
    }

    public <T> T getService(String name) {
        return (T) services.get(name);
    }

    private void loadService(String name) {
        try {
            Class<?> clazz = Class.forName(name);
            if (clazz.isAnnotationPresent(Service.class)) {
                if (clazz.isAnnotationPresent(Lazy.class)) {
                    saveLazyService(clazz);
                } else {
                    saveService(clazz);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void saveLazyService(Class<?> clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        StringBuilder lazyClassname = new StringBuilder();
        lazyClassname.append(clazz.getPackage().getName());
        lazyClassname.append(".Lazy");
        lazyClassname.append(clazz.getSimpleName());

        Class<?> lazyClass = Class.forName(lazyClassname.toString());

        services.put(
                getServiceName(clazz),
                lazyClass.newInstance()
        );
    }

    private void saveService(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        services.put(
                getServiceName(clazz),
                clazz.newInstance()
        );
    }

    private void injectServices() {
        services.values().forEach(service -> {
            Arrays.stream(service.getClass().getDeclaredFields()).forEach( field -> {
                Service serviceAnnotation = field.getAnnotation(Service.class);
                if (serviceAnnotation != null) {
                    try {
                        Method setter = service.getClass().getMethod("set" + capitalize(field.getName()), field.getType());
                        setter.invoke(service, services.get(serviceAnnotation.name()));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private String getServiceName(Class<?> clazz) {
        return clazz.getAnnotation(Service.class).name();
    }

}
