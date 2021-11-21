package top.zsmile.jvm.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XClassLoader extends ClassLoader {

    public static void main(String[] args) throws ClassNotFoundException {
        String className = "Hello";
        String methodName = "hello";
        ClassLoader xClassLoader = new XClassLoader();
        Class<?> helloClass = xClassLoader.loadClass(className);

        try {
            Object o = helloClass.newInstance();
            Method method = helloClass.getMethod(methodName);
            method.invoke(o);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final String suffix = ".xlass";
        InputStream resourceAsStream = this.getClass().getResourceAsStream(name + suffix);

        try {
            int available = resourceAsStream.available();
            byte[] bytes = new byte[available];
            resourceAsStream.read(bytes);

            byte[] decode = decode(bytes);
            return defineClass(name, decode, 0, decode.length);
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

    public byte[] decode(byte[] code) {
        byte[] decodeBytes = new byte[code.length];
        for (int i = 0; i < code.length; i++) {
            decodeBytes[i] = (byte) (255 - code[i]);
        }
        return decodeBytes;
    }
}
