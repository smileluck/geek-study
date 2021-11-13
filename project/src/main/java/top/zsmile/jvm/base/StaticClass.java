package top.zsmile.jvm.base;

public class StaticClass {
    static {
        System.out.println("StaticClass static");
    }

    public static void main(String[] args) {
        System.out.println(StaticTestClass.staticNum2);
        StaticTestClass.test1();
        StaticTestClass aClass = new StaticTestClass();
        aClass.test2();
        System.out.println("============");
        new StaticClass2().printValue();
    }
}

class StaticClass2 {
    static int value = 20;

    public void printValue() {
        int value = 2;
        System.out.println("printValue：" + value);
        System.out.println("printValue：" + this.value);
    }
}

class StaticFather {
    static {
        System.out.println("father static codes run");
    }

    {
        System.out.println("father codes run");
    }

    StaticFather() {
        System.out.println("father constructor");
    }
}

class StaticTestClass extends StaticFather {
    static int staticNum;
    static int staticNum2 = 10;
    int num;
    int num2 = 11;

    static {
        System.out.println("static codes run");
    }

    {
        System.out.println("codes run");
    }


    public static void test1() {
        System.out.println("static test");
    }

    public void test2() {
        System.out.println("test");
        test1();
    }

    StaticTestClass() {
        System.out.println("constructor");
        this.test2();
    }
}
