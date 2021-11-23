package top.zsmile.jvm.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeTest {

    public static void main(String[] args) {
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        Object o = new Object();
        unsafe.putInt(o, 0L, 1);
        System.out.println(o);

        unsafe.putInt(o, 1L, 111);
        System.out.println(o);

    }

}
