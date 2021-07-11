package top.zsmile.nio.netty.server.router;

import java.util.concurrent.atomic.AtomicInteger;

public class PollHttpRouter implements HttpRouter {


    @Override
    public String router(String[] routers) {
        int index = RouterServerConstant.index.getAndAdd(1);
        return routers[index % routers.length];
    }

}
