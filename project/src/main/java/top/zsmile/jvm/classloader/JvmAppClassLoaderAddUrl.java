package top.zsmile.jvm.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JvmAppClassLoaderAddUrl {
    public static void main(String[] args) {
        String appPath = "file:/d:/app/";
        URLClassLoader urlClassLoader = (URLClassLoader) JvmAppClassLoaderAddUrl.class.getClassLoader();
        try {
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);

            addURL.setAccessible(true);
            URL url = new URL(appPath);
            addURL.invoke(urlClassLoader, url);
            Class.forName("top.zsmile.jvm.classloader.Hello"); // 效果跟Class.forName("jvm.Hello").newInstance()一样
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
