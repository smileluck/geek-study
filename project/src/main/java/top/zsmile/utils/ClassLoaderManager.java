package top.zsmile.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClassLoaderManager {

    private static ClassLoaderManager manager = new ClassLoaderManager();

    // common classloader
    private static CommonClassloader commonClassloader;

    // save classloader
    protected ConcurrentHashMap<String, Class<?>> loaderCache = new ConcurrentHashMap<>();

    private ClassLoaderManager() {
//        CommonClassloader commonClassloader = new CommonClassloader();
    }

    static class CommonClassloader extends URLClassLoader {
        // need decode class, have base64 decode
        protected String decodeType = null;

        public CommonClassloader(URL[] urls) {
            super(urls);
        }

        public CommonClassloader(URL[] urls, String decodeType) {
            this(urls);
            this.decodeType = decodeType;
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

}
