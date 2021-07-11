package top.zsmile.nio.netty.server.inbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import top.zsmile.nio.netty.server.filter.HttpRequestFilter;
import top.zsmile.nio.netty.server.filter.HttpRequestRequestFilterImpl;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("注入header");
        FullHttpRequest request = (FullHttpRequest) msg;
        HttpRequestRequestFilterImpl httpRequestRequestFilter = new HttpRequestRequestFilterImpl();
        httpRequestRequestFilter.filter(request);
        super.channelRead(ctx, msg);
    }
}
