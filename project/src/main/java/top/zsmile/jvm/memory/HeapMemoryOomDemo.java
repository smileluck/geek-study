package top.zsmile.jvm.memory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 分配堆内存溢出
 */
public class HeapMemoryOomDemo {
    static int _100Mb = 1024*1024*100;

    public static void main(String[] args) {
        List<ByteBuffer> byteBuffers = new ArrayList<>();
        int i=0;
        try {
            while (true) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(_100Mb);
                byteBuffers.add(byteBuffer);
                i++;
            }
        }finally {
            System.out.println(i);
        }
    }
}
