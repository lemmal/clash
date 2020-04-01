package com.clash.bean;

import com.clash.logger.ClashLogger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class BeanParser {
    private Map<Class<?>, Class<?>> constructs = new HashMap<>();
    private Map<Class<?>, Class<? extends IBeanProvider<?>>> providers = new HashMap<>();
    private Set<Class<?>> consumers = new HashSet<>();

    private static final String PACKAGE_BEGIN = "com";
    private static final String CLASS_END = ".class";
    private static final String PATH_SPLITTER = File.separator;
    private static final String PACKAGE_SPLITTER = ".";

    BeanParser() {

    }

    public Map<Class<?>, Class<?>> getConstructs() {
        return constructs;
    }

    public Map<Class<?>, Class<? extends IBeanProvider<?>>> getProviders() {
        return providers;
    }

    public Set<Class<?>> getConsumers() {
        return consumers;
    }

    public BeanParser parseClasses(List<String> paths) throws BeanParseException {
        List<File> files = getClassFiles(paths);
        for (File classFile : files) {
            String path = classFile.getAbsolutePath();
            if(!path.endsWith(CLASS_END)) {
                continue;
            }
            try {
                String fullClassName = path2FullClassName(path);
                Class<?> clazz = Class.forName(fullClassName);
                if(null == clazz) {
                    continue;
                }
                //getClassFiles包含了生成的子类文件，不需要额外处理子类的逻辑
                parseConstruct(clazz);
                parseProvider(clazz);
                parseConsumer(clazz);
            } catch (Exception e) {
                throw new BeanParseException(e);
            }
        }
        return this;
    }

    private void parseConstruct(Class<?> clazz) {
        if(null == clazz.getAnnotation(BeanConstruct.class)) {
            return;
        }
        if(isConstructorExist(clazz)) {
            return;
        }
        _addBeanConstruct(clazz);
    }

    @SuppressWarnings("unchecked")
    private void parseProvider(Class<?> clazz) {
        if(!IBeanProvider.class.isAssignableFrom(clazz)) {
            return;
        }
        BeanProvider provider = clazz.getAnnotation(BeanProvider.class);
        if(null == provider) {
            return;
        }
        if(constructs.containsKey(provider.value()) || providers.containsKey(provider.value())) {
            throw new IllegalArgumentException(String.format("duplicate construct or provider : %s", provider.value().getName()));
        }
        providers.put(provider.value(), (Class<? extends IBeanProvider<?>>) clazz);
    }

    private void parseConsumer(Class<?> clazz) {
        if(null == clazz.getAnnotation(BeanConsumer.class)) {
            return;
        }
        consumers.add(clazz);
    }

    private List<File> getClassFiles(List<String> paths) {
        List<File> files = new LinkedList<>();
        for (String path : paths) {
            URL url = getURL(path);
            if(null == url) {
                continue;
            }
            fetchFiles(files, url);
        }
        return files;
    }

    private void fetchFiles(List<File> files, URL url) {
        File file = new File(url.getPath());
        if(!file.isDirectory()) {
            return;
        }
        File[] tmpFiles = file.listFiles();
        if(null == tmpFiles) {
            return;
        }
        //广度优先
        files.addAll(Arrays.asList(tmpFiles));
        for (File tmp : tmpFiles) {
            try {
                fetchFiles(files, tmp.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private URL getURL(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    private boolean isConstructorExist(Class<?> clazz) {
        try {
            return null == clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            ClashLogger.error("no default constructor found, class: {}", clazz.getName());
            return false;
        }
    }

    private void _addBeanConstruct(Class<?> clazz) {
        BeanConstruct annotation = clazz.getAnnotation(BeanConstruct.class);
        if(constructs.containsKey(annotation.value()) || providers.containsKey(annotation.value())) {
            throw new IllegalArgumentException(String.format("duplicate construct or provider : %s", annotation.value().getName()));
        }
        constructs.put(annotation.value(), clazz);
    }

    private String path2FullClassName(String path) {
        int begin = path.indexOf(PACKAGE_BEGIN);
        int end = path.indexOf(CLASS_END);
        String sub = path.substring(begin, end);
        return sub.replace(PATH_SPLITTER, PACKAGE_SPLITTER);
    }
}
