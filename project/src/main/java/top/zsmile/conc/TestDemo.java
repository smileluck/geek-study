package top.zsmile.conc;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

class Test {

    public static int[] demo() {
        int[] result = new int[10000];
        result[0] = result[1] = 1;
        for (int i = 2; i < 100; i++) {
            result[i] = result[i - 2] + result[i - 1];
        }
        return result;

    }
}

public class TestDemo {

    public static void main(String[] args) {
//        Test test = new Test();
        new Thread(() -> {
            int[] demo = Test.demo();
            AtomicReference<String> allStr = new AtomicReference<>("");
            Arrays.stream(demo).forEach((str -> {
                allStr.set(allStr.get() + "," + str);
            }));
            System.out.println(allStr.get());
        }).start();

        int[] demo = Test.demo();
        AtomicReference<String> allStr = new AtomicReference<>("");
        Arrays.stream(demo).forEach((str -> {
            allStr.set(allStr.get() + "," + str);
        }));
        System.out.println(allStr.get());
    }
}
