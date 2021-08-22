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

    /**
     * okhttp
     * @param req
     * @return
     * @throws IOException
     */
//    public RpcfxResponse post(RpcfxRequest req) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .url(url)
//                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), JSON.toJSONString(req)))
//                .build();
//        String respJson = client.newCall(request).execute().body().string();
//        System.out.println("resp json: " + respJson);
//        return JSON.parseObject(respJson, RpcfxResponse.class);
//    }
}
