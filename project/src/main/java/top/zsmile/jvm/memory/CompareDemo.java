package top.zsmile.jvm.memory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 比较DirectByteBuffer和HeapByteBuffer
 */
public class CompareDemo {
    private static final int capacity = 1024*1024*100;
    private static final int len = 10;

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(1000);
        System.gc();
        createDirectMemory();
        Thread.sleep(1000);
        System.gc();
        Thread.sleep(1000);
        createHeapMemory();

    }

    public static void createDirectMemory() {
        long start = System.currentTimeMillis();

        ByteBuffer[] byteBuffers = new ByteBuffer[len];
        for (int i = 0; i < len; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);
            byteBuffers[i] = byteBuffer;
        }
        long end = System.currentTimeMillis();
        System.out.println("DirectByteBuffer创建完成，耗时：" + (end - start));
    }

    public static void createHeapMemory() {
        long start = System.currentTimeMillis();
        ByteBuffer[] byteBuffers = new ByteBuffer[len];
        for (int i = 0; i < len; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
            byteBuffers[i] = byteBuffer;
        }
        long end = System.currentTimeMillis();
        System.out.println("HeapByteBuffer创建完成，耗时：" + (end - start));

    }
}
