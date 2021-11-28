package top.zsmile.utils;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderManager {

    private static ClassLoaderManager manager = new ClassLoaderManager();

    private static CommonClassloader commonClassloader;

    private ClassLoaderManager() {
//        CommonClassloader commonClassloader = new CommonClassloader();
    }

    static class CommonClassloader extends URLClassLoader {
        public CommonClassloader(URL[] urls) {
            super(urls);
        }
    }

}
