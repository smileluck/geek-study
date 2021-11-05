package top.zsmile.jvm.base;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

final class FinalSuperClass {
    private int privateA = 0;
    public int publicB = 2;
    public final int publicCFinal = 3;
    public static final int publicDFinalStatic = 1;
}

class SuperClass {
    private int privateA = 0;
    public int publicB = 2;
    public final int publicCFinal = 3;
    public static final int publicDFinalStatic = 1;
    public static final String publicSFinalStatic = "hello world";
    public static final List<String> publicArrFinalStatic = new ArrayList<>();

    final void sayHello() {
        System.out.println("hello");
    }

    void sayHello2() {
        System.out.println("hello2");
    }
}

/**
 * extends FinalSuperClass 编译异常
 */
public class FinalClass extends SuperClass {

    @Override
    void sayHello2() {
        super.sayHello2();
    }

    // 通过static初始化
    private final static int testStatic;

    static {
        testStatic = 10;
    }

    // 需要注意的是某些场景下需要抛出异常
    private final static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //这里需要抛出异常，否则编译错误。
            throw new RuntimeException(e);
        }
    }

    // 通过构造器初始化
    private final int testConstruct;

    //  final不能修饰构造方法
    //final public FinalClass(int num,int num2){}

    public FinalClass(int num) {
        testConstruct = num;
    }

    public static void main(String[] args) {
        //private 无法继承
        //this.privateA = 1;

        System.out.println(publicDFinalStatic);
        // final修饰的基本类型无法修改值。
        //publicDFinalStatic = 2;

        System.out.println(publicSFinalStatic);
        // final修饰的引用对象无法修改指向。
        //publicSFinalStatic = "hello";


        // final修饰的引用对象可以修改里面包含的属性
        publicArrFinalStatic.add("hello");
        publicArrFinalStatic.add("word");
        System.out.println(publicArrFinalStatic.toString());


        // 通过构造器初始化final属性可以在运行期设置值，更加灵活
        FinalClass finalClass = new FinalClass(1);
        System.out.println("finalClass1 num :" + finalClass.testConstruct);
        FinalClass finalClass2 = new FinalClass(2);
        System.out.println("finalClass2 num :" + finalClass2.testConstruct);

        /**
         * final修饰的方法可以被继承，但不能覆盖
         */
        finalClass.sayHello();

    }
}
