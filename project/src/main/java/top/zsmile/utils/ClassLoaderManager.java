package top.zsmile.utils;

import sun.misc.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassLoaderManager {

    private static ClassLoaderManager manager = new ClassLoaderManager();

    // common classloader
    public static CommonClassloader commonClassloader;

    // save classloader
    protected ConcurrentHashMap<String, Class<?>> loaderCache = new ConcurrentHashMap<>();

    static {
        commonClassloader = new CommonClassloader(new URL[]{});
    }

    static class CommonClassloader extends URLClassLoader {
        // need decode class, have base64 decode
        protected String decodeType = null;

        private JarURLConnection jarURLConnection = null;

        public CommonClassloader(URL[] urls) {
            super(urls);
        }

        @Override
        protected void addURL(URL url) {
            super.addURL(url);
        }

        public CommonClassloader(URL[] urls, String decodeType) {
            this(urls);
            this.decodeType = decodeType;
        }

        public void reloadUrl() {
            System.out.println("reloadAllJar");
            URL[] urls = this.getURLs();
            for (URL url : urls) {
                System.out.println("reload Url => " + url.getPath());
                openJarConn(url);
                reloadClass();
            }
        }

        public void reloadClass() {
            try {
                JarFile jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.isDirectory()) continue;

                    String name = jarEntry.getName();
                    if (name.endsWith(".class")) {
                        byte[] bytes = ClassLoaderUtils.decodeClass.checkDecode(decodeType, jarFile, jarEntry);
                        if (bytes == null) {
                            throw new ClassNotFoundException();
                        }
                        defineClass(name, bytes, 0, bytes.length);
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }

        public void closeJarConn() {
            if (jarURLConnection != null) {
                try {
                    JarFile jarFile = jarURLConnection.getJarFile();
                    jarFile.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

        }

        public void openJarConn(URL url) {
            try {
                closeJarConn();
                jarURLConnection = (JarURLConnection) url.openConnection();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public URL findResource(String name) {
            String path = name.replace('.', '/').concat(".class");
            return super.findResource(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {

            InputStream resourceAsStream = this.getResourceAsStream(name);
            try {
                int available = resourceAsStream.available();
                byte[] bytes = new byte[available];
                resourceAsStream.read(bytes);
                byte[] decode = ClassLoaderUtils.decodeClass.checkDecode(this.decodeType, bytes);
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
        URL url = new URL("jar:file:/C:\\Users\\Admin\\Desktop\\HelloWord.jar!/");

        ClassLoaderManager.commonClassloader.addURL(url);
//        ClassLoaderManager.commonClassloader.reloadUrl();

        commonClassloader.loadClass("top.zsmile.jvm.classloader.HelloWord");
    }
}
