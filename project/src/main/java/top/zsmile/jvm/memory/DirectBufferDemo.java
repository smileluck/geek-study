package top.zsmile.jvm.memory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DirectBufferDemo {
    public static void main(String[] args) {
        ByteBuffer demoDirectByteBuffer = ByteBuffer.allocateDirect(8);
        printBufferProperties("write to demoDirectByteBuffer before ", demoDirectByteBuffer);
        // put to buffer 5 bytes utf-8 编码
        demoDirectByteBuffer.put("hello".getBytes());
        // put to buffer 4 bytes utf-8 编码
//        demoDirectByteBuffer.put("word".getBytes());// 超出8字节，溢出
        printBufferProperties("after write to demoDirectByteBuffer ", demoDirectByteBuffer);
        // invoke flip
        demoDirectByteBuffer.flip();
        printBufferProperties("after invoke flip ", demoDirectByteBuffer);

//        demoDirectByteBuffer.put("word".getBytes());
//
//        demoDirectByteBuffer.clear();
//        printBufferProperties("after invoke clear ", demoDirectByteBuffer);
//
//        demoDirectByteBuffer.put("word".getBytes());

        demoDirectByteBuffer.compact();
        printBufferProperties("after invoke compact ", demoDirectByteBuffer);
        demoDirectByteBuffer.position(3);
        printBufferProperties("after invoke compact ", demoDirectByteBuffer);
        demoDirectByteBuffer.mark();
        printBufferProperties("after invoke compact ", demoDirectByteBuffer);

        demoDirectByteBuffer.position(5);
        printBufferProperties("after invoke compact ", demoDirectByteBuffer);
        demoDirectByteBuffer.reset();
        printBufferProperties("after invoke compact ", demoDirectByteBuffer);


        byte[] temp = new byte[demoDirectByteBuffer.limit()];
        int index = 0;
        while (demoDirectByteBuffer.hasRemaining()) {
            temp[index] = demoDirectByteBuffer.get();
            index++;
        }
        printBufferProperties("after read from demoDirectByteBuffer", demoDirectByteBuffer);

        System.out.println(new String(temp));
    }

    private static void printBufferProperties(String des, ByteBuffer target) {
        System.out.println(String.format("%s--position:%d,limit:%d,capacity:%s", des,
                target.position(), target.limit(), target.capacity()));
    }
}
