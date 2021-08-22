[toc]

---

# 题目

**1.（选做）**实现简单的 Protocol Buffer/Thrift/gRPC(选任一个) 远程调用 demo。
**2.（选做）**实现简单的 WebService-Axis2/CXF 远程调用 demo。
**3.（必做）**改造自定义 RPC 的程序，提交到 GitHub：

- 尝试将服务端写死查找接口实现类变成泛型和反射；
- 尝试将客户端动态代理改成 AOP，添加异常处理；
- 尝试使用 Netty+HTTP 作为 client 端传输方式。

**4.（选做☆☆））**升级自定义 RPC 的程序：

- 尝试使用压测并分析优化 RPC 性能；
- 尝试使用 Netty+TCP 作为两端传输方式；
- 尝试自定义二进制序列化；
- 尝试压测改进后的 RPC 并分析优化，有问题欢迎群里讨论；
- 尝试将 fastjson 改成 xstream；
- 尝试使用字节码生成方式代替服务端反射。

**5.（选做）**按课程第二部分练习各个技术点的应用。
**6.（选做）**按 dubbo-samples 项目的各个 demo 学习具体功能使用。
**7.（必做）**结合 dubbo+hmily，实现一个 TCC 外汇交易处理，代码提交到 GitHub:

- 用户 A 的美元账户和人民币账户都在 A 库，使用 1 美元兑换 7 人民币 ;
- 用户 B 的美元账户和人民币账户都在 B 库，使用 7 人民币兑换 1 美元 ;
- 设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。

**8.（挑战☆☆）**尝试扩展 Dubbo

- 基于上次作业的自定义序列化，实现 Dubbo 的序列化扩展 ;
- 基于上次作业的自定义 RPC，实现 Dubbo 的 RPC 扩展 ;
- 在 Dubbo 的 filter 机制上，实现 REST 权限控制，可参考 dubbox;
- 实现一个自定义 Dubbo 的 Cluster/Loadbalance 扩展，如果一分钟内调用某个服务 / 提供者超过 10 次，则拒绝提供服务直到下一分钟 ;
- 整合 Dubbo+Sentinel，实现限流功能 ;
- 整合 Dubbo 与 Skywalking，实现全链路性能监控。

# 作业三

- 尝试将服务端写死查找接口实现类变成泛型和反射；

```java
public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        // 作业1：改成泛型和反射
//        Object service = resolver.resolve(serviceClass);//this.applicationContext.getBean(serviceClass);


        try {
            Class<?> aClass = Class.forName(serviceClass);
//            Object o = aClass.newInstance();
//            Method method = aClass.getMethod(request.getMethod());
//            Object result = method.invoke(aClass, request.getParams());
            Method method = resolveMethodFromClass(aClass, request.getMethod());
            Object result = method.invoke(resolver.resolve(aClass.getName()), request.getParams()); // dubbo, fastjson,
            // 两次json序列化能否合并成一个
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
            response.setException(e);
            response.setStatus(false);
            return response;
        }
    }
```

- 尝试将客户端动态代理改成 AOP，添加异常处理；

```java
    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new RpcAopMethodInterceptor(serviceClass, url));
        return (T) enhancer.create();
    }
```

```java
package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

public class RpcAopMethodInterceptor implements MethodInterceptor {
    private String url;
    private Class<?> serviceClass;

    public RpcAopMethodInterceptor(Class<?> serviceClass, String url) {
        this.url = url;
        this.serviceClass = serviceClass;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RpcfxRequest rpcfxRequest = new RpcfxRequest();
        rpcfxRequest.setMethod(method.getName());
        rpcfxRequest.setParams(objects);
        rpcfxRequest.setServiceClass(serviceClass.getName());
        RpcfxResponse response = post(rpcfxRequest);
        return JSON.parse(response.getResult().toString());
    }

    /**
     * okhttp http
     *
     * @param req
     * @return
     * @throws IOException
     */
    public RpcfxResponse post(RpcfxRequest req) throws IOException {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), JSON.toJSONString(req)))
                .build();
        String respJson = client.newCall(request).execute().body().string();
        System.out.println("resp json: " + respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }
}
```

- 尝试使用 Netty+HTTP 作为 client 端传输方式。

```java
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
```

```java
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
```

使用netty客户端访问

```java
package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

public class RpcAopMethodInterceptor implements MethodInterceptor {
    private String url;
    private Class<?> serviceClass;

    public RpcAopMethodInterceptor(Class<?> serviceClass, String url) {
        this.url = url;
        this.serviceClass = serviceClass;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RpcfxRequest rpcfxRequest = new RpcfxRequest();
        rpcfxRequest.setMethod(method.getName());
        rpcfxRequest.setParams(objects);
        rpcfxRequest.setServiceClass(serviceClass.getName());
        RpcfxResponse response = post(rpcfxRequest);
        return JSON.parse(response.getResult().toString());
    }

    /**
     * netty http
     *
     * @param req
     * @return
     * @throws IOException
     */
    public RpcfxResponse post(RpcfxRequest req) throws IOException {
        NettyHttpClient nettyHttpClient = new NettyHttpClient("localhost", 8080, url);
        RpcfxResponse post = nettyHttpClient.post(JSON.toJSONString(req));
        return post;
    }
}
```

# 作业七

