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

public class XJarClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        return null;
    }


    /**
     * @param jarPath params form => jar:file:/${path}!/
     * @throws IOException
     */
    public void getJarFile(String jarPath) throws IOException {
        URL url = new URL(jarPath);
        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
        if (urlConnection != null) {
            JarFile jarFile = urlConnection.getJarFile();

            Manifest manifest = urlConnection.getManifest();


            Map<String, Attributes> entries1 = manifest.getEntries();
            for (Map.Entry<String, Attributes> entry : entries1.entrySet()) {
                System.out.println(entry.getKey() + "=>" + entry.getValue());
            }

            ZipEntry entry = jarFile.getEntry("META-INF/MANIFEST.MF");
            System.out.println(entry);

            // get main-Class attr
            String mainClassName = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
            System.out.println("main-class:" + mainClassName);

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                System.out.println(jarEntry.getName() + " is directory ：" + jarEntry.isDirectory());
                if (jarEntry.getName().endsWith(".class")) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    int available = inputStream.available();
                    byte[] bytes = new byte[available];
                    inputStream.read(bytes);
                    Class<?> aClass = defineClass("top.zsmile.jvm.classloader.Hello", bytes, 0, bytes.length);
//                    resolveClass(aClass);
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

        }
    }

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
        new XJarClassLoader().getJarFile("jar:file:/E:\\ZHQ\\project\\0626\\geek-study2\\geek-study\\project\\src\\main\\java\\top\\zsmile\\jvm\\classloader\\Hello.jar!/");
//        new XJarClassLoader().getJarFile("file:/E:\\ZHQ\\project\\0626\\geek-study2\\geek-study\\project\\src\\main\\java\\top\\zsmile\\jvm\\classloader\\Hello.xar");
    }
}
