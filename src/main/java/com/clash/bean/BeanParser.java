package com.clash.bean;

import com.clash.logger.ClashLogger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public enum BeanParser {
    INSTANCE;

    private static final String PACKAGE_BEGIN = "com";
    private static final String CLASS_END = ".class";
    private static final String PATH_SPLITTER = File.separator;
    private static final String PACKAGE_SPLITTER = ".";

    public Map<Class<?>, Constructor<?>> parse(String... paths) throws BeanParseException {
        List<File> classFiles = getClassFiles(paths);
        return classFile2Constructor(classFiles);
    }

    private List<File> getClassFiles(String... paths) {
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

    private Map<Class<?>, Constructor<?>> classFile2Constructor(List<File> classFiles) throws BeanParseException {
        Map<Class<?>, Constructor<?>> handlers = new HashMap<>();
        if(null == classFiles) {
            return handlers;
        }
        try {
            for (File classFile : classFiles) {
                String path = classFile.getAbsolutePath();
                if(!path.endsWith(CLASS_END)) {
                    continue;
                }
                String fullClassName = path2FullClassName(path);
                Class<?> clazz = Class.forName(fullClassName);
                if(null == clazz || null == clazz.getAnnotation(BeanConstruct.class)) {
                    continue;
                }
                Constructor<?> constructor = clazz.getConstructor();
                if(null == constructor) {
                    ClashLogger.error("no default constructor found, class: {}", clazz.getName());
                    continue;
                }
                handlers.put(clazz, constructor);
            }
            return handlers;
        } catch (Exception e) {
            throw new BeanParseException(e);
        }
    }

    private String path2FullClassName(String path) {
        int begin = path.indexOf(PACKAGE_BEGIN);
        int end = path.indexOf(CLASS_END);
        String sub = path.substring(begin, end);
        return sub.replace(PATH_SPLITTER, PACKAGE_SPLITTER);
    }
}
