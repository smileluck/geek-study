package top.zsmile.nio.buffer;

import java.nio.ByteBuffer;

public class CreateBufferDemo {
    public static void main(String[] args) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(8);
        System.out.println(allocateDirect.hasArray() + "-" + allocateDirect);

        ByteBuffer allocate = ByteBuffer.allocate(8);
        System.out.println(allocate.hasArray() + "-" + allocate);

        byte[] array = new byte[10];
        ByteBuffer wrap = ByteBuffer.wrap(array);
        System.out.println(wrap.hasArray() + "-" + wrap);

        ByteBuffer wrap1 = ByteBuffer.wrap(array, 4, 3);
        System.out.println(wrap1.hasArray() + "-" + wrap1);


    }
}
