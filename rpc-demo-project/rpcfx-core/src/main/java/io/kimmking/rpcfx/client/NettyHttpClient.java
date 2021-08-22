package io.kimmking.rpcfx.client;

import io.kimmking.rpcfx.api.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;

public class NettyHttpClient {
    private EventLoopGroup boss = new NioEventLoopGroup();

    private String host;
    private int port;
    private String url;

    public NettyHttpClient(String host, int port, String url) {
        this.host = host;
        this.port = port;
        this.url = url;
    }

    public RpcfxResponse post(String jsonBody) {
        HttpClientHandler httpClientHandler = new HttpClientHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new HttpClientCodec())
                                    .addLast(new HttpObjectAggregator(2048))
//                                    .addLast(new HttpContentDecompressor())
                                    .addLast(httpClientHandler);
                        }
                    });
            ChannelFuture future = bootstrap.connect(this.host, this.port).sync();
            FullHttpRequest fullHttpRequest = null;
            URI uri = new URI(this.url);
            fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString(), Unpooled.wrappedBuffer(jsonBody.getBytes()));
            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                    .set(HttpHeaderNames.HOST, host)
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());

            future.channel().writeAndFlush(fullHttpRequest);

            future.channel().closeFuture().sync();
            return httpClientHandler.getResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return httpClientHandler.getResult();
        } finally {
            boss.shutdownGracefully();
        }
    }
}
