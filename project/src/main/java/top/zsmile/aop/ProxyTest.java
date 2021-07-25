package top.zsmile.aop;

import java.lang.reflect.Proxy;

public class ProxyTest {
    public static void main(String[] args) {
        Person person = new PersonMan();

        InvokePersonHandler invokePersonHandler = new InvokePersonHandler(person);
        ClassLoader classLoader = person.getClass().getClassLoader();
        Class<?>[] interfaces = person.getClass().getInterfaces();

        Person person1 = (Person) Proxy.newProxyInstance(classLoader, interfaces, invokePersonHandler);

        System.out.println("动态代理类型：" + person1.getClass().getName());
        String s = person1.sayHello("123");
        System.out.println("sayHello:" + s);
    }
}
