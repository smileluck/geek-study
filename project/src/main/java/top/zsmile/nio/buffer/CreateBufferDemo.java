package top.zsmile.nio.buffer;

import java.nio.ByteBuffer;

public class CreateBufferDemo {
    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(8);
        System.out.println(allocate);


        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(8);
        System.out.println(allocateDirect);

        byte[] array = new byte[10];
        ByteBuffer wrap = ByteBuffer.wrap(array);
        System.out.println(wrap);

        ByteBuffer wrap1 = ByteBuffer.wrap(array, 4, 3);
        System.out.println(wrap1);

    }
}
