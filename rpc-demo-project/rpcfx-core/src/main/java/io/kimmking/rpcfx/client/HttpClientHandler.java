package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    private RpcfxResponse result;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
        String string = fullHttpResponse.content().toString(CharsetUtil.UTF_8);
        RpcfxResponse response = JSON.parseObject(string,
                RpcfxResponse.class);
        result = response;
        ctx.close();
    }

    public RpcfxResponse getResult() {
        return this.result;
    }
}
