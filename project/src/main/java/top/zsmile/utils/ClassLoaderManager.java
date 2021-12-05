package top.zsmile.utils;

import lombok.SneakyThrows;
import sun.misc.Resource;
import top.zsmile.jvm.classloader.JarUrlClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: B.Smile
 * @Date: 2021/12/5 19:21
 * @Description: 1. 类 ClassLoaderManage 作为对自定义加载器的管理类
 * 2. 类 ClassLoaderUtils 作为自定义加载器所需要的工具类
 * 3. 将 ClassLoader 分成2类：
 * 1. 作为公共库。加载公共类库，只有一个。
 * 2. 作为模块类。加载独立模块，允许多个加载器加载，方便隔离。
 * 4. 模块类需要加载公共类方法时，抛出给 公共库加载器加载。
 */

public class ClassLoaderManager {

    private ClassLoaderManager() {

    }

//    private final static ClassLoaderManager INSTANCE = new ClassLoaderManager();

    private final static String COMMONO_LIB_PREFIX = "top.zsmile.jvm.classloader";

    // common classloader ,is parent classloader
    private static CommonClassloader libClassloader;

    // save classloader, is child classloader
    private static ConcurrentHashMap<String, CommonClassloader> loaderCache = new ConcurrentHashMap<>();

    static {
        libClassloader = new CommonClassloader(true);
    }

    public static CommonClassloader getLibLoader() {
        return libClassloader;
    }

    public static CommonClassloader createClassloader(String loaderName) {
        CommonClassloader commonClassloader = new CommonClassloader(ClassLoaderManager.libClassloader);
        loaderCache.put(loaderName, commonClassloader);
        return commonClassloader;
    }

    static class CommonClassloader extends ClassLoader {


        private boolean isLib = false;

        List<URL> urls = new ArrayList<>();

        public CommonClassloader() {

        }

        public CommonClassloader(boolean isLib) {
            this.isLib = isLib;
        }

        public CommonClassloader(CommonClassloader commonClassloader) {
            super(commonClassloader);
        }

        public CommonClassloader(CommonClassloader commonClassloader, boolean isLib) {
            super(commonClassloader);
            this.isLib = isLib;
        }

        public void addURL(String path) throws MalformedURLException {
            File file = new File(path);
            ClassLoaderUtils.loopFiles(this, file);
        }

        @SneakyThrows
        @Override
        public InputStream getResourceAsStream(String name) {
            System.out.println("classloader info :" + this.getClass());
            InputStream inputStream = null;
            if (name.startsWith(COMMONO_LIB_PREFIX) && !isLib) {
                inputStream = ClassLoaderManager.libClassloader.getResourceAsStream(name);
            }
            if (inputStream != null) {
                return inputStream;
            }
            for (URL url : urls) {
                try {
                    if ("jar".equals(url.getProtocol())) {
                        JarURLConnection jarURLConnection1 = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection1.getJarFile();
                        JarEntry jarEntry = jarFile.getJarEntry(name.replaceAll("\\.", "/").concat(".class"));
                        if (jarEntry != null) {
                            return jarFile.getInputStream(jarEntry);
                        }
                    }
//                    else {
//                        if (url.getPath().endsWith(".class")) {
//                            InputStream fin = new FileInputStream(url.getFile());
//                            return fin;
//                        }
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.startsWith(COMMONO_LIB_PREFIX)) {
                Class<?> loadedClass = getLibLoader().findLoadedClass(name);
                if (loadedClass != null) {
                    return loadedClass;
                }
                return getLibLoader().findClass(name);
            } else if (name.startsWith("top.zsmile.jvm")) {
                Class<?> loadedClass = findLoadedClass(name);
                if (loadedClass != null) {
                    return loadedClass;
                }
                return findClass(name);
            }
            return super.loadClass(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {

            InputStream resourceAsStream = this.getResourceAsStream(name);
            try {
                int available = resourceAsStream.available();
                byte[] bytes = new byte[available];
                resourceAsStream.read(bytes);
                byte[] decode = ClassLoaderUtils.codes.checkDecode(null, bytes);
                if (decode == null) {
                    throw new ClassNotFoundException();
                }
                Class<?> aClass = defineClass(name, decode, 0, decode.length);
                return aClass;
            } catch (IOException e) {
                e.printStackTrace();
                throw new ClassNotFoundException();
            } finally {
                if (resourceAsStream != null) {
                    try {
                        resourceAsStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
//        URL url = new URL();
        CommonClassloader commonClassloader = ClassLoaderManager.getLibLoader();
        commonClassloader.addURL("C:\\Users\\Admin\\Desktop\\HelloWord.jar");

        CommonClassloader test = ClassLoaderManager.createClassloader("test");
        test.addURL("C:\\Users\\Admin\\Desktop\\HelloWordTest.jar");

//        Class<?> aClass = commonClassloader.loadClass("top.zsmile.jvm.classloader.HelloWord");

        // test load lib class, print Hello World1
//        Class<?> aClass = test.loadClass("top.zsmile.jvm.classloader.HelloWord");

        // test load non-lib class, print Hello World2
        Class<?> aClass = test.loadClass("top.zsmile.jvm.classloader.HelloWord");
        aClass = test.loadClass("top.zsmile.jvm.classloader.HelloWord");
        Method method = null;
        try {
            method = aClass.getMethod("main", String[].class);
            method.invoke(null, (Object) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
