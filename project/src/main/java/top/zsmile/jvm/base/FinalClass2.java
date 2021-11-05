package top.zsmile.jvm.base;


class TestFinalClass {
    // 不会触发初始化
    static final int num = 99;

    // 触发初始化
    static int num2 = 100;

    static {
        System.out.println("visit class final static var，not init class");
    }
}

public class FinalClass2 {
    public static void main(String[] args) {
        System.out.println(TestFinalClass.num);
        System.out.println(TestFinalClass.num2);
    }
}
