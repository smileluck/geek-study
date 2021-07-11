package top.zsmile.nio.netty.server.inbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.zsmile.nio.netty.server.router.PollHttpRouter;
import top.zsmile.nio.netty.server.router.RouterServerConstant;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

public class HttpExecHandler extends ChannelInboundHandlerAdapter {

    private PollHttpRouter pollHttpRouter = new PollHttpRouter();

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        String uri = request.uri();
        if (uri.contains("/test")) {
            List<String> s = new QueryStringDecoder(uri).parameters().get("s");
            helloText(request, channelHandlerContext, s != null && s.size() != 0 ? s.toString() : "no exist params");
        } else if (uri.contains("/favicon.ico")) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            channelHandlerContext.write(response);
        } else {
            String[] split = uri.split("/");
            helloText2(request, channelHandlerContext, split[split.length - 1]);
        }
//        super.channelRead(channelHandlerContext, msg);
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
                    channelHandlerContext.pipeline().write(response).addListeners(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    channelHandlerContext.pipeline().write(response);
                }
            }
        }
    }

    private void helloText2(FullHttpRequest fullHttpRequest, ChannelHandlerContext channelHandlerContext, String text) {
        FullHttpResponse response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            String url = pollHttpRouter.router(RouterServerConstant.routerList);
            System.out.println("当前URL:" + url);
            Request request = new Request.Builder().url(url + "/test?s=" + text).get().build();
            Response execute = okHttpClient.newCall(request).execute();
//            System.out.println("响应内容为：" + execute.body().string());
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(execute.body().bytes()));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, execute.header(HttpHeaderNames.CONTENT_TYPE.toString()));
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        } catch (Exception exception) {
            System.out.println("处理异常处理异常：" + exception.getMessage());
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        } finally {
            if (fullHttpRequest != null) {
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    channelHandlerContext.pipeline().write(response).addListeners(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    channelHandlerContext.pipeline().write(response);
                }
            }
        }
    }

}
