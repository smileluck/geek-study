package top.zsmile.nio.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import top.zsmile.nio.netty.server.inbound.HttpExecHandler;
import top.zsmile.nio.netty.server.inbound.HttpRequestHandler;
import top.zsmile.nio.netty.server.outbound.HttpResponseHandler;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));


        //作业1
//        pipeline.addLast(new HttpHandler02());

        //作业2
//        pipeline.addLast(new HttpHandler03());

        //作业3
        pipeline.addLast(new HttpRequestHandler());
        pipeline.addLast(new HttpExecHandler());
        pipeline.addLast(new HttpResponseHandler());

    }
}
