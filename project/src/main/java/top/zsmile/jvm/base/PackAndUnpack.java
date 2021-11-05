package top.zsmile.jvm.base;

/**
 * 装箱拆箱
 */
public class PackAndUnpack {
    public static void main(String[] args) {
        //这里相等，是因为文件在编译时，会将有一个静态常量池的概念，也就是说，此时这里的hello都是指向同一个内存地址
        System.out.println("hello" == "hello");
        System.out.println("hello".equals("hello"));
        System.out.println("hello".equals("test"));

        System.out.println("====");
        Integer num = new Integer(1);
        Integer num2 = 1;
        int num3 = 1;
        System.out.println(num == num2);
        System.out.println(num3 == num);
        System.out.println(num3 == num2);

        num = Integer.valueOf(1);
        num2 = 1;
        System.out.println(num == num2);

        num = new Integer(129);
        num2 = 129;
        num3 = 129;
        System.out.println(num == num2);
        System.out.println(num3 == num);
        System.out.println(num3 == num2);
    }
}
