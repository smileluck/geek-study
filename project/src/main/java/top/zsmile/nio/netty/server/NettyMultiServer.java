package top.zsmile.nio.netty.server;

public class NettyMultiServer {
    public static void main(String[] args) {
        int[] ports = {8088, 8089};
        for (int i : ports) {
            new Thread(() -> {
                NettyHttpServer.start(i);
            }).start();
        }
    }
}
