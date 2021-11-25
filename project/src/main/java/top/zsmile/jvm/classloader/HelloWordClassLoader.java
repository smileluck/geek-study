package top.zsmile.jvm.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class HelloWordClassLoader extends ClassLoader {
    public static void main(String[] args) throws IOException, Exception {
        HelloWord.sayHello(); // 输出 hello

        Scanner in = new Scanner(System.in);
        HelloWordClassLoader helloWordClassLoader = new HelloWordClassLoader();
        Class<?> helloClass = helloWordClassLoader.loadClass("HelloWord");
        helloWordClassLoader.resolveClass(helloClass);
        Object o = helloClass.newInstance();
        Method sayHello = helloClass.getMethod("sayHello");
        sayHello.invoke(null);// 输出hello2

        while (!in.next().equalsIgnoreCase("exit")) {
            System.out.println(123);
            HelloWord.sayHello(); //输出hello , 想替换掉AppClassloader里面HelloWord类，让他输出hello2
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (!name.equalsIgnoreCase("HelloWord2")) {
            return super.loadClass(name);
        }
        return findClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        InputStream resourceAsStream = null;
        try {
//            URL url = new URL("file:/E:\\ZHQ\\project\\0626\\geek-study2\\geek-study\\project\\target\\classes\\top\\zsmile\\jvm\\classloader\\HelloWord.class");

            resourceAsStream = this.getClass().getResourceAsStream("HelloWord2.class");
            int available = resourceAsStream.available();
            byte[] bytes = new byte[available];
            resourceAsStream.read(bytes);


            return defineClass("top.zsmile.jvm.classloader.HelloWord", bytes, 0, bytes.length);
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
