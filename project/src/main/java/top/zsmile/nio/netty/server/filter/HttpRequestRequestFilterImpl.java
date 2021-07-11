package top.zsmile.nio.netty.server.filter;

import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestRequestFilterImpl implements HttpRequestFilter {

    @Override
    public void filter(FullHttpRequest request) {
        request.headers().add("header-smile", "smilex");
    }
}
