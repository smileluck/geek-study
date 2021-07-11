package top.zsmile.nio.netty.server.router;

import java.util.concurrent.atomic.AtomicInteger;

public class RouterServerConstant {

    public static AtomicInteger index = new AtomicInteger(0);

    public static String[] routerList = new String[]{"http://localhost:8088", "http://localhost:8089"};

}
