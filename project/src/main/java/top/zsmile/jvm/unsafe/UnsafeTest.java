package top.zsmile.jvm.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeTest {

    public static void main(String[] args) {
        Unsafe unsafe = UnsafeUtils.getUnsafe();
    }

}
