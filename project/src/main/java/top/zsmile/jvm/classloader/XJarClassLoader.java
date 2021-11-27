package top.zsmile.jvm.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class XJarClassLoader extends URLClassLoader {
    public XJarClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final String suffix = ".xlass";
        InputStream resourceAsStream = this.getResourceAsStream(name + suffix);

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

    public static void main(String[] args) throws Exception {
        URL url = new URL("file:/D:\\project\\B.Smile\\geek-study1\\project\\src\\main\\java\\top\\zsmile\\jvm\\classloader\\Hello.xar");
        XJarClassLoader urlClassLoader = new XJarClassLoader(new URL[]{url});
        Class<?> hello = urlClassLoader.loadClass("Hello");
        Object o = hello.newInstance();
        Method sayHello = hello.getMethod("hello");
        sayHello.invoke(o);
    }
}
