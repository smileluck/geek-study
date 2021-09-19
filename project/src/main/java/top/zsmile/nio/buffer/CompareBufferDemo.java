package top.zsmile.nio.buffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class CompareBufferDemo {
    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(10);
        ByteBuffer allocate1 = ByteBuffer.allocate(8);
        ByteBuffer direct10 = ByteBuffer.allocateDirect(10);
        ByteBuffer allocate2 = ByteBuffer.allocate(10);

        allocate.put("hello".getBytes());
        allocate1.put("hello".getBytes());
        allocate2.put("hello".getBytes());
        direct10.put("hello".getBytes());

        System.out.println("allocate10 equals allocate8 => " + allocate.equals(allocate1));
        System.out.println("allocate10 equals allocate10-2 => " + allocate.equals(allocate2));
        System.out.println("allocate10 equals direct10 => " + allocate.equals(direct10));

    }
}
