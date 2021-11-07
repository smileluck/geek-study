package top.zsmile.jvm.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author: B.Smile
 * @Date: 2021/11/4 11:26
 * @Description: Unsafe工具类
 */
public final class UnsafeUtils {

    private static final Unsafe unsafe;

    static {
        Field field = null;
        try {
            field = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        try {
            unsafe = (Unsafe) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("unsafe init error");
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
