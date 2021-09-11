package top.zsmile.jvm.memory;

import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 使用Unsafe分配和释放内存
 */
public class DirectmemoryUnsafeDemo {
    private final static int _100MB = 1024 * 1024 * 500;

    public static void main(String[] args) throws IOException {
        Unsafe unsafe = getUnsafe();
        long base = unsafe.allocateMemory(_100MB);
        unsafe.setMemory(base, _100MB, (byte) 0);
        System.out.println("分配内存成功");
        System.in.read();
        unsafe.freeMemory(base);
        System.out.println("释放内存");
        System.in.read();
    }

    public static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            return unsafe;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
