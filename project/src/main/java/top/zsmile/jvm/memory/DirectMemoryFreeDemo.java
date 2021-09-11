package top.zsmile.jvm.memory;

import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 通过Cleaner释放内存
 */
    public class DirectMemoryFreeDemo {
        private static int _500MB = 1024 * 1024 * 500;

        public static void main(String[] args) throws IOException {
            long start = System.currentTimeMillis();
            System.out.println("开始分配");
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_500MB);
            long endtime = System.currentTimeMillis();

            System.out.println("分配完成,耗时：" + (endtime - start));
            System.in.read();
            if (byteBuffer.isDirect()) {
                System.out.println("byteBuffer是直接内存");
                ((DirectBuffer) byteBuffer).cleaner().clean();
            }
            System.out.println("执行Cleaner，程序未完全退出");
            System.in.read();
            System.out.println("程序退出");
        }
    }
