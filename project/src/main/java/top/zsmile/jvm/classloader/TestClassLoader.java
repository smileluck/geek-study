package top.zsmile.jvm.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author: B.Smile
 * @Date: 2021/11/26 17:24
 * @Description: test classloader character
 */
public class TestClassLoader {

    public static void main(String[] args) {
        TestClassLoader testClassLoader = new TestClassLoader();
        testClassLoader.isolate();
    }

    /**
     * Test isolate
     */
    public void isolate() {
        try {
            final String jarPath = "jar:file:/D:\\project\\B.Smile\\geek-study1\\project\\src\\main\\java\\top\\zsmile\\jvm\\classloader\\HelloWord.jar!/";
            final String jarPath2 = "file:/D:\\project\\B.Smile\\geek-study1\\project\\src\\main\\java\\top\\zsmile\\jvm\\classloader\\HelloWord-copy.jar";
            final String className = "top.zsmile.jvm.classloader.HelloWord";

            ClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, getClass().getClassLoader());
            ClassLoader urlClassLoader2 = new URLClassLoader(new URL[]{new URL(jarPath2)}, urlClassLoader);

            System.out.println("HelloWord.jar ======" + urlClassLoader);
            Class<?> aClass = Class.forName(className, true, urlClassLoader);
            runMain(aClass);
            Object o = aClass.newInstance();
            System.out.println();


            System.out.println("HelloWord2.jar ====== " + urlClassLoader2);
            Class<?> aClass2 = Class.forName(className, true, urlClassLoader2);
            runMain(aClass2);
            Object o2 = aClass2.newInstance();
            System.out.println();

            System.out.println("judge helloWord.class equals helloWord2.class");
            System.out.println(aClass.equals(aClass2));


            System.out.println("parent：" + urlClassLoader.getParent() + "," + urlClassLoader2.getParent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runMain(Class mainClass) {
        try {
            Method main = mainClass.getMethod("main", String[].class);
            main.invoke(null, (Object) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
