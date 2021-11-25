package top.zsmile.jvm.classloader;

import sun.plugin.cache.JarCacheUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 *  @author: B.Smile
 *  @Date: 2021/11/25 17:08
 *  @Description: load jar and run main-class
 */
public class JarRunMainClass extends ClassLoader {


    public void getJarFileByClassLoader(String filePath) {
        try {
            URL url = new URL(filePath);
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
            Class<?> hello = urlClassLoader.loadClass("top.zsmile.jvm.classloader.Hello");
            Method method = hello.getMethod("main", String[].class);
            method.invoke(null, (Object) null);
        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

//        new XJarClassLoader().getJarFile("file:/E:\\ZHQ\\project\\0626\\geek-study2\\geek-study\\project\\src\\main\\java\\top\\zsmile\\jvm\\classloader\\Hello.xar");
    }
}
