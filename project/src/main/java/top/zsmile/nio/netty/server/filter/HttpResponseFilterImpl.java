package top.zsmile.nio.netty.server.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public class HttpResponseFilterImpl implements HttpResponseFilter {
    @Override
    public void filter(FullHttpResponse response) {
        response.headers().add("header-smile", "non-smilex");
    }
}
