package top.zsmile.aop;

public class PersonMan implements Person {

    @Override
    public String sayHello(String name) {
        return "hello" + name;
    }

    @Override
    public String sayBay() {
        return "good bye";
    }
}
