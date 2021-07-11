package top.zsmile.nio.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    private FullHttpResponse result;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
//        ByteBuf content = fullHttpResponse.content();
//        result = content.toString(CharsetUtil.UTF_8);
//        System.out.println("接收到的信息：" + result);
        result = fullHttpResponse;
//        content.release();
        ctx.close();
    }

    public FullHttpResponse getResult() {
        return this.result;
    }
}
