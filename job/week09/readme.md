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

测试代码：[项目代码地址](https://github.com/smileluck/geek-study/blob/main/rpc-demo-project/)，[Client代码地址](https://github.com/smileluck/geek-study/blob/main/rpc-demo-project/rpcfx-demo-consumer/src/main/java/io/kimmking/rpcfx/demo/consumer/RpcfxClientApplication.java)， [Server代码地址](https://github.com/smileluck/geek-study/blob/main/rpc-demo-project/rpcfx-demo-provider/src/main/java/io/kimmking/rpcfx/demo/provider/RpcfxServerApplication.java)

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

作业代码： [项目代码地址](https://github.com/smileluck/geek-study/blob/main/dubbo-hmily-project/)

- 数据库geek_bank0，geek_bank1

  ```sql
  /*
  Navicat MySQL Data Transfer
  
  Source Server         : localhost_3306
  Source Server Version : 50722
  Source Host           : localhost:3306
  Source Database       : geek_bank
  
  Target Server Type    : MYSQL
  Target Server Version : 50722
  File Encoding         : 65001
  
  Date: 2021-08-22 23:26:21
  */
  
  SET FOREIGN_KEY_CHECKS=0;
  
  -- ----------------------------
  -- Table structure for tb_user_balance
  -- ----------------------------
  DROP TABLE IF EXISTS `tb_user_balance`;
  CREATE TABLE `tb_user_balance` (
    `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `cash_type` varchar(10) NOT NULL COMMENT '钱种类',
    `money` decimal(10,0) NOT NULL DEFAULT '0' COMMENT '金额w',
    `frozen_money` decimal(10,0) NOT NULL DEFAULT '0' COMMENT '冻结金额',
    PRIMARY KEY (`user_id`,`cash_type`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
  
  -- ----------------------------
  -- Records of tb_user_balance
  -- ----------------------------
  INSERT INTO `tb_user_balance` VALUES ('1', 'dollar', '10', '0');
  INSERT INTO `tb_user_balance` VALUES ('1', 'rmb', '70', '0');
  
  -- ----------------------------
  -- Table structure for tb_user
  -- ----------------------------
  DROP TABLE IF EXISTS `tb_user`;
  CREATE TABLE `tb_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(50) NOT NULL COMMENT '用户名称',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
  
  -- ----------------------------
  -- Records of tb_user
  -- ----------------------------
  INSERT INTO `tb_user` VALUES ('1', 'A');
  
  ```

- 账户A Service，通过DubboReference获取账户B Service

```java
package top.zsmile.dubbo.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.core.context.HmilyTransactionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.zsmile.dubbo.provider.dao.UserBalanceDao;
import top.zsmile.dubbo.entity.UserBalanceEntity;
import top.zsmile.dubbo.provider.service.UserBalanceService;

import java.math.BigDecimal;

/**账户A
*/
@DubboService(version = "1.0.0", tag = "bankA", weight = 100)
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceDao, UserBalanceEntity> implements UserBalanceService {


    @DubboReference(version = "2.0.0", tag = "bankB") //, url = "dubbo://127.0.0.1:12345")
    private UserBalanceService userBalanceServiceB;

    private final Long userId = 1L;

    @Override
    @HmilyTCC(confirmMethod = "tradeConfirm", cancelMethod = "tradeCancel")
    public boolean trade(BigDecimal money) throws Exception {

        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        if (dollarAccount.getMoney().intValue() >= money.intValue()) {
            update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", dollarAccount.getMoney().subtract(money)).set("frozen_money", dollarAccount.getFrozenMoney().add(money)));
            userBalanceServiceB.trade(money);
//            OkHttpClient client = new OkHttpClient();
//            final Request request = new Request.Builder()
//                    .url("http://localhost:8082/bank2/test/trade?money=" + money.intValue())
//                    .build();
//            String res = client.newCall(request).execute().body().string();
//            if (null == res || res.equalsIgnoreCase("false")) {
//                throw new Exception("trade fail");
//            }
            return true;
        } else {
            throw new Exception("no money");
        }
    }

    @Override
    public void tradeConfirm(BigDecimal money) {
        System.out.println("confirm trade bankA");
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("frozen_money", dollarAccount.getFrozenMoney().subtract(money)));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("money", rmbAccount.getMoney().add(money.multiply(new BigDecimal(7)))));
    }

    @Override
    public void tradeCancel(BigDecimal money) {
        System.out.println("cancel trade bankA");
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", dollarAccount.getMoney().add(money)).set("frozen_money", dollarAccount.getFrozenMoney().subtract(money)));
    }
}
```

```java
package top.zsmile.dubbo.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.transaction.annotation.Transactional;
import top.zsmile.dubbo.entity.UserBalanceEntity;
import top.zsmile.dubbo.provider.dao.UserBalanceDao;
import top.zsmile.dubbo.provider.service.UserBalanceService;

import java.math.BigDecimal;

/**账户B
*/
@DubboService(version = "2.0.0", tag = "bankB", weight = 100)
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceDao, UserBalanceEntity> implements UserBalanceService {

    private final Long userId = 2L;

    @Override
    @HmilyTCC(confirmMethod = "tradeConfirm", cancelMethod = "tradeCancel")
    public boolean trade(BigDecimal money) throws Exception {
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        BigDecimal rmbDollar = money.multiply(new BigDecimal(7));
        if (rmbAccount.getMoney().intValue() >= rmbDollar.intValue()) {
            update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("money", rmbAccount.getMoney().subtract(rmbDollar)).set("frozen_money", rmbAccount.getFrozenMoney().add(rmbDollar)));
            return true;
        } else {
//            return false;
            throw new Exception("no money");
        }
    }

    @Override
    public void tradeConfirm(BigDecimal money) {
        System.out.println("confirm trade bankB");
        BigDecimal rmbDollar = money.multiply(new BigDecimal(7));
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("frozen_money", dollarAccount.getFrozenMoney().subtract(rmbDollar)));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", rmbAccount.getMoney().add(money)));
    }

    @Override
    public void tradeCancel(BigDecimal money) {
        System.out.println("cancel trade bankB");
        BigDecimal rmbDollar = money.multiply(new BigDecimal(7));
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("money", rmbAccount.getMoney().add(rmbDollar)).set("frozen_money", dollarAccount.getFrozenMoney().subtract(rmbDollar)));
//        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", rmbAccount.getMoney().add(money)));
    }
}
```



因为没有加上幂等和防悬挂的等机制，容易造成多次执行转账，后面再研究，完善一下。
