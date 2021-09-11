package top.zsmile.jvm.memory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 验证System.gc是否能够释放直接内存
 */
public class DirectMemoryGcDemo {
    private static int _500MB = 1024 * 1024 * 500;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        System.out.println("开始分配");
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_500MB);
        long endtime = System.currentTimeMillis();

        System.out.println("分配完成,耗时：" + (endtime - start));
        System.in.read();
//        byteBuffer = null;
        System.gc();
        System.out.println("执行System.gc，程序未完全退出");
        System.in.read();
        System.out.println("程序退出");
    }
}
