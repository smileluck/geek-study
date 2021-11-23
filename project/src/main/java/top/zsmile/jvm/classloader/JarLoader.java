package top.zsmile.jvm.classloader;

import sun.misc.ClassLoaderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class JarLoader extends ClassLoader {

    /**
     * @param jarPath params form => jar:file:/${path}!/
     * @throws IOException
     */
    public void getJarFile(String jarPath) throws IOException {
        URL url = new URL(jarPath);
        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
        if (urlConnection != null) {
            JarFile jarFile = urlConnection.getJarFile();

            printManifest(urlConnection);

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

    public void printManifest(JarURLConnection urlConnection) {
        System.out.println("loop manifest");
        try {
            Manifest manifest = null;
            manifest = urlConnection.getManifest();

            System.out.println("manifest Attributes:");
            Attributes mainAttributes = manifest.getMainAttributes();
            Set<Map.Entry<Object, Object>> entries = mainAttributes.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                System.out.println(entry.getKey() + "=>" + entry.getValue());
            }

            System.out.println("manifest entries:");
            Map<String, Attributes> entries1 = manifest.getEntries();
            for (Map.Entry<String, Attributes> entry : entries1.entrySet()) {
                System.out.println(entry.getKey() + "=>" + entry.getValue());
            }

            // get main-Class attr
            String mainClassName = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
            System.out.println("main-class:" + mainClassName);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        new JarLoader().getJarFile("jar:file:/D:\\project\\B.Smile\\geek-study1\\project\\src\\main\\java\\top\\zsmile\\jvm\\classloader\\Hello.jar!/");
    }
}
