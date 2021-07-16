package top.zsmile.conc;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeWork02 {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTaskCall futureTaskCall = new FutureTaskCall();
        FutureTask<String> futureTask = new FutureTask<>(futureTaskCall);
        new Thread(futureTask).start();
        System.out.println("FutureTaskCall返回值：" + futureTask.get());


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> executorFuture = executorService.submit(futureTaskCall);
        System.out.println("executorFuture返回值：" + executorFuture.get());


        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(HomeWork02::sum);
        System.out.println("completableFuture返回值：" + cf.get());

        AtomicInteger atomicInteger = new AtomicInteger();
        Thread thread = new Thread(() -> {
            atomicInteger.set(sum());
        });
        thread.start();
        thread.join();
        System.out.println("atomicInteger返回值：" + atomicInteger.get());


        Thread thread1 = new Thread(() -> {
            intValue = sum();
        });
        thread1.start();
        while (intValue == null) {
        }
        System.out.println("intValue返回值：" + atomicInteger.get());


        SynThread synThread = new SynThread();
        Thread thread2 = new Thread(() -> {
            synThread.sum();
        });
        thread2.start();
        System.out.println("SynThread返回值：" + synThread.getValue());


        CountDownLatch countDownLatch = new CountDownLatch(1);
        CountDownlatchTask countDownlatchTask = new CountDownlatchTask(countDownLatch);
        Thread thread3 = new Thread(() -> {
            countDownlatchTask.sum();
        });
        thread3.start();
        System.out.println("countDownlatchTask返回值：" + countDownlatchTask.getValue());


        SemaphoreTask semaphoreTask = new SemaphoreTask();
        Thread thread4 = new Thread(() -> {
            try {
                semaphoreTask.sum();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread4.start();
        System.out.println("semaphoreTask返回值：" + semaphoreTask.getValue());

        CyclicBarrierTask cyclicBarrierTask = new CyclicBarrierTask();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, () -> {
            System.out.println("cyclicBarrierTask返回值：" + cyclicBarrierTask.getValue());
        });
        new Thread(() -> {
            try {
                cyclicBarrierTask.sum();
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }).start();

    }

    static class CyclicBarrierTask {

        private volatile Integer value = null;

        public void sum() {
            value = fibo(36);
        }

        private static int fibo(int a) {
            if (a < 2)
                return 1;
            return fibo(a - 1) + fibo(a - 2);
        }

        public Integer getValue() {
            return this.value;
        }
    }

    static class SemaphoreTask {

        private volatile Integer value = null;
        private Semaphore semaphore = new Semaphore(1);

        public void sum() throws InterruptedException {
            semaphore.acquire();
            value = fibo(36);
            semaphore.release();
        }

        private static int fibo(int a) {
            if (a < 2)
                return 1;
            return fibo(a - 1) + fibo(a - 2);
        }

        public Integer getValue() throws InterruptedException {
            Integer c = null;
            while (this.value == null) {
            }
            semaphore.acquire();
            c = this.value;
            semaphore.release();
            return c;

        }
    }


    static class CountDownlatchTask {

        private volatile Integer value = null;
        private CountDownLatch latch = null;

        public CountDownlatchTask(CountDownLatch countDownLatch) {
            this.latch = countDownLatch;
        }

        public void sum() {
            value = fibo(36);
            latch.countDown();
        }

        private static int fibo(int a) {
            if (a < 2)
                return 1;
            return fibo(a - 1) + fibo(a - 2);
        }

        public Integer getValue() throws InterruptedException {
            latch.await();
            return value;
        }
    }

    static class SynThread {

        private volatile Integer value = null;

        synchronized public void sum() {
            value = fibo(36);
            notify();
        }

        private static int fibo(int a) {
            if (a < 2)
                return 1;
            return fibo(a - 1) + fibo(a - 2);
        }

        synchronized public Integer getValue() throws InterruptedException {
            while (value == null) {
                wait();
            }
            return value;
        }
    }


    public volatile static Integer intValue = null;

    static class FutureTaskCall implements Callable<String> {
        @Override
        public String call() throws Exception {
            int num = sum();
            return num + "";
        }
    }


    private static int sum() {
        Random random = new Random();
        return fibo(random.nextInt(20) + 1);
    }

    private static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }

}
