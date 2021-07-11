package top.zsmile.nio.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

public class HttpHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        try {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) o;
            String uri = fullHttpRequest.uri();
            if (uri.contains("/test")) {
                helloText(fullHttpRequest, channelHandlerContext, "test");
            } else {
                helloText(fullHttpRequest, channelHandlerContext, "all");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(o);
        }
    }

    private void helloText(FullHttpRequest fullHttpRequest, ChannelHandlerContext channelHandlerContext, String text) {
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(text.getBytes()));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        } catch (Exception exception) {
            System.out.println("处理异常：" + exception.getMessage());
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        } finally {
            if (fullHttpRequest != null) {
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    channelHandlerContext.write(response).addListeners(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    channelHandlerContext.write(response);
                }
            }
        }
    }
}
