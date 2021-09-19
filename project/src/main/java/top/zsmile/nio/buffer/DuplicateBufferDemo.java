package top.zsmile.nio.buffer;

import java.nio.IntBuffer;

public class DuplicateBufferDemo {
    public static void main(String[] args) {
        IntBuffer buffer = IntBuffer.allocate(10);
        buffer.put(new int[]{0, 1, 2, 3, 4});
        System.out.println("create buffer：" + buffer);
        printBuffer(buffer, true);

        IntBuffer duplicate1 = buffer.duplicate();
        System.out.println("duplicate buffer：" + duplicate1);
        printBuffer(duplicate1, true);

        duplicate1.put(0, 7);
        System.out.println("origin and duplicate buffer start=====");
        printBuffer(buffer, true);
        printBuffer(duplicate1, true);
        System.out.println("origin and duplicate buffer end=====");


        IntBuffer duplicate2 = buffer.asReadOnlyBuffer();
        System.out.println("asReadOnlyBuffer buffer：" + duplicate2);
        buffer.put(0, 8);
        printBuffer(duplicate2, true);

        try {
            duplicate2.put(0, 6);
        } catch (Exception e) {
            System.err.println("asReadOnlyBuffer buffer update Exception:" + e.toString());
        }

        System.out.println("slice buffer before,origin buffer status：" + buffer);
        buffer.position(3);
        System.out.println("slice buffer before,origin buffer position 3 after：" + buffer);
        IntBuffer slice = buffer.slice();
        System.out.println("slice buffer：" + slice);
        printBuffer(slice, false);

        buffer.put(3, 13);
        buffer.rewind();
        slice.put(0, 23);
        System.out.println("origin buffer:" + buffer);
        printBuffer(buffer, false);
        System.out.println("slice buffer：" + slice);
        printBuffer(slice, true);
    }

    public static void printBuffer(IntBuffer buffer, boolean needFlip) {
        if (needFlip) {
            buffer.flip();
        }
        System.out.print("buffer print：");
        while (buffer.hasRemaining()) {
            System.out.print(buffer.get() + ",");
        }
        System.out.println();
    }
}
