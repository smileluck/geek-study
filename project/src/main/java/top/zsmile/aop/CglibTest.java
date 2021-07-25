package top.zsmile.aop;

public class CglibTest {
    public static void main(String[] args) {
        CglibProxy proxy = new CglibProxy();
        PersonMan1 personMan1 = (PersonMan1) proxy.getProxy(PersonMan1.class);

        System.out.println("动态代理类型：" + personMan1.getClass().getName());
        String say = personMan1.say();
        System.out.println("personSay: " + say);
    }
}
