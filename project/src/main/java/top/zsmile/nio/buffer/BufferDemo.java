package top.zsmile.nio.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BufferDemo {
    public static void main(String[] args) {
        IntBuffer buffer = IntBuffer.allocate(10);
        System.out.println("create buffer：" + buffer);

        buffer.put(new int[]{1, 2, 3, 4, 5});
        System.out.println("put buffer after：" + buffer);

        buffer.put(0, 6);
        System.out.println("update buffer after：" + buffer);

        buffer.flip();
        System.out.println("flip buffer after：" + buffer);

        printBuffer(buffer);

        buffer.position(2);
        System.out.println("update buffer position：" + buffer);

        buffer.mark();
        System.out.println("mark buffer position：" + buffer);

        printBuffer(buffer);
        System.out.println("print buffer after：" + buffer);

        buffer.reset();
        System.out.println("reset buffer position：" + buffer);

        buffer.rewind();
        System.out.println("rewind buffer position&mark：" + buffer);

        printBuffer(buffer);
        buffer.position(2);
        System.out.println("compact buffer before：" + buffer);
        buffer.compact();
        System.out.println("compact buffer after：" + buffer);


        printBuffer(buffer);
        System.out.println("print buffer after：" + buffer);

        buffer.position(0);
        printBuffer(buffer);
        System.out.println("print buffer after：" + buffer);

    }

    public static void printBuffer(IntBuffer buffer) {
        System.out.print("buffer print：");
        while (buffer.hasRemaining()) {
            System.out.print(buffer.get());
        }
        System.out.println();
    }
}
