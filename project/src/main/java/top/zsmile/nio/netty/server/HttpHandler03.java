package top.zsmile.nio.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.zsmile.nio.netty.client.NettyHttpClient;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

public class HttpHandler03 extends ChannelInboundHandlerAdapter {

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
                List<String> s = new QueryStringDecoder(uri).parameters().get("s");
                helloText(fullHttpRequest, channelHandlerContext, s != null && s.size() != 0 ? s.toString() : "no exist params");
            } else if (uri.contains("/favicon.ico")) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
                channelHandlerContext.write(response);
            } else {
                String[] split = uri.split("/");
                helloText2(fullHttpRequest, channelHandlerContext, split[split.length - 1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(o);
        }
    }


    private void helloText2(FullHttpRequest fullHttpRequest, ChannelHandlerContext channelHandlerContext, String text) {
        FullHttpResponse response = null;
        try {
            NettyHttpClient nettyHttpClient = new NettyHttpClient("127.0.0.1", 8088, "/test?s=" + text);
            response = nettyHttpClient.get();
        } catch (Exception exception) {
            System.out.println("处理异常处理异常：" + exception.getMessage());
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        } finally {
            if (fullHttpRequest != null) {
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    channelHandlerContext.write(response).addListeners(ChannelFutureListener.CLOSE);
                } else {
                    channelHandlerContext.write(response);
                }
            }
        }
    }

    private void helloText(FullHttpRequest fullHttpRequest, ChannelHandlerContext channelHandlerContext, String text) {
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(text.getBytes()));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML);
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        } catch (Exception exception) {
            System.out.println("处理异常处理异常：" + exception.getMessage());
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
