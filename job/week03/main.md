[toc]

---



# 题目

1.（必做）整合你上次作业的 httpclient/okhttp；
2.（选做）使用 netty 实现后端 http 访问（代替上一步骤）
3.（必做）实现过滤器。
4.（选做）实现路由。
5.（选做）跑一跑课上的各个例子，加深对多线程的理解
6.（选做）完善网关的例子，试着调整其中的线程池参数



# 作业1

作业路径：project/src/main/java/top/zsmile/nio/netty/server/HttpHandler02.java

接口说明：

- /test 用于直接返回参数s的值。
- 其它接口，都是将最后一个/后的字符串作为参数，请求/test接口。





# 作业2

作业路径：project/src/main/java/top/zsmile/nio/netty/server/HttpHandler03.java

接口说明：

- /test 用于直接返回参数s的值。
- 其它接口，都是将最后一个/后的字符串作为参数，请求/test接口。



# 作业3

作业3采用的是inbound的和outbound的方式，将filter添加到handler，然后根据in和out的执行顺序，让在请求前添加header[header-smile]=smilex，然后响应后，修改header[header-smile]=non-smilex。

修改文件：project/src/main/java/top/zsmile/nio/netty/server/HttpInitializer.java

```java
pipeline.addLast(new HttpRequestHandler());
pipeline.addLast(new HttpExecHandler());
pipeline.addLast(new HttpResponseHandler());
```



# 作业4

作业路径：project/src/main/java/top/zsmile/nio/netty/server/inbound/HttpExecHandler.java

接口说明：

- /test 用于直接返回参数s的值。
- 其它接口，都是将最后一个/后的字符串作为参数，请求/test接口。在这里会轮询发送8088或者8089的服务