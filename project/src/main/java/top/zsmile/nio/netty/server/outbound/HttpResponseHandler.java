package top.zsmile.nio.netty.server.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import top.zsmile.nio.netty.server.filter.HttpResponseFilterImpl;

public class HttpResponseHandler extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
        HttpResponseFilterImpl httpResponseFilter = new HttpResponseFilterImpl();
        httpResponseFilter.filter(fullHttpResponse);
        ctx.writeAndFlush(msg);
//        super.write(ctx, msg, promise);
    }
}
