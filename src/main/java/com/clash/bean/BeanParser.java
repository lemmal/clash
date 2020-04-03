package com.clash.bean;

import com.clash.logger.ClashLogger;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

public class BeanParser {
    private Table<Class<?>, String, Class<?>> constructs = HashBasedTable.create();
    private Table<Class<?>, String, IBeanProvider<?>> providers = HashBasedTable.create();
    private Set<Class<?>> consumers = new HashSet<>();

    private static final String PACKAGE_BEGIN = "com";
    private static final String CLASS_END = ".class";
    private static final String PATH_SPLITTER = File.separator;
    private static final String PACKAGE_SPLITTER = ".";

    BeanParser() {

    }

    public Table<Class<?>, String, Class<?>> getConstructs() {
        return constructs;
    }

    public Table<Class<?>, String, ? extends IBeanProvider<?>> getProviders() {
        return providers;
    }

    public Set<Class<?>> getConsumers() {
        return consumers;
    }

    public BeanParser parseClasses(List<String> paths) throws BeanParseException {
        List<String> filePaths = paths.stream().map(p -> p.replace(PACKAGE_SPLITTER, PATH_SPLITTER)).collect(Collectors.toList());
        List<File> files = getClassFiles(filePaths);
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
    private void parseProvider(Class<?> clazz) throws BeanParseException {
        if(clazz.equals(IBeanProvider.class) || !IBeanProvider.class.isAssignableFrom(clazz)) {
            return;
        }
        Class<?> targetInterface = getProviderInterface(clazz);
        if(null == targetInterface) {
            throw new IllegalArgumentException(String.format("generic type not found : %s", clazz.getName()));
        }
        try {
            IBeanProvider<?> provider = ((Class<? extends IBeanProvider<?>>) clazz).newInstance();
            if(constructs.contains(targetInterface, provider.name()) || providers.contains(targetInterface, provider.name())) {
                throw new IllegalArgumentException(String.format("duplicate construct or provider : %s", targetInterface.getName()));
            }
            providers.put(targetInterface, provider.name(), provider);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanParseException(e);
        }
    }

    private Class<?> getProviderInterface(Class<?> clazz) {
        for (Type type : clazz.getGenericInterfaces()) {
            ParameterizedType pt = (ParameterizedType) type;
            if(pt.getRawType().equals(IBeanProvider.class)) {
                Type arg = pt.getActualTypeArguments()[0];
                return (Class<?>) arg;
            }
        }
        return null;
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
        File file = new File(decode(url.getPath()));
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

    private String decode(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException();
        }
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
        Class<?> targetInterface = annotation.value();
        String name = annotation.name();
        if(constructs.contains(targetInterface, name) || providers.contains(targetInterface, name)) {
            throw new IllegalArgumentException(String.format("duplicate construct or provider : %s", annotation.value().getName()));
        }
        constructs.put(targetInterface, name, clazz);
    }

    private String path2FullClassName(String path) {
        int begin = path.indexOf(PACKAGE_BEGIN);
        int end = path.indexOf(CLASS_END);
        String sub = path.substring(begin, end);
        return sub.replace(PATH_SPLITTER, PACKAGE_SPLITTER);
    }
}
