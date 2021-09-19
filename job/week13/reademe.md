[toc]

---

# 题目

1. 搭建一个3节点kafka集群，测试功能和性能；实现springkafka下对kafka集群的操作。
2. 思考和设计自定义MQ第二个版本或第三个版本，写代码实现其中至少一个功能点， 把设计思路和实现代码，提交到Github。





# 作业1

## 下载kafka 2.71

下载连接：https://www.apache.org/dyn/closer.cgi?path=/kafka/2.7.1/kafka_2.12-2.7.1.tgz

![image-20210918145312245](D:\project\B.Smile\geek-study1\job\week13\image-20210918145312245.png)



## 配置集群

配置文件地址：[server9011.properties]()，[server9012.properties]()，[server9013.properties]()

1. 在server.properties的基础上，复制3份文件，重命名为server9011.properties，server9012.properties，server9013.properties。
2. 修改文件内容。

```properties
# server9011.properties
listeners=PLAINTEXT://:9011
log.dirs=D:/software/kafka_2.13-2.7.1/kafka-logs1
# server9012.properties
listeners=PLAINTEXT://:9012
log.dirs=D:/software/kafka_2.13-2.7.1/kafka-logs2
# server9013.properties
listeners=PLAINTEXT://:9012
log.dirs=D:/software/kafka_2.13-2.7.1/kafka-logs3
```

3. 启动服务

```shell
 # 要先启动zookeeper服务
 .\bin\windows\zookeeper-server-start.bat  .\config\zookeeper.properties
 
 # 依次启动kafks服务
 .\bin\windows\kafka-server-start.bat .\config\server9011.properties
 .\bin\windows\kafka-server-start.bat .\config\server9012.properties
 .\bin\windows\kafka-server-start.bat .\config\server9013.properties

```



### 异常记录

启动kafka时，会出现异常：这时因为zookeeper里面的信息有冲突。我们将zookeeper数据清空，并将kafka-logs*的文件夹下的文件删除，重新启动即可。

```java
ERROR Fatal error during KafkaServer startup. Prepare to shutdown (kafka.server.KafkaServer)
kafka.common.InconsistentClusterIdException: The Cluster ID Rv30bYQFSMWb6wFHgx3EMw doesn't match stored clusterId Some(KGRNd5JtSxSIb-U-P8VZ5A) in meta.properties. The broker is trying to join the wrong cluster. Configured zookeeper.connect may be wrong.
        at kafka.server.KafkaServer.startup(KafkaServer.scala:252)
        at kafka.server.KafkaServerStartable.startup(KafkaServerStartable.scala:44)
        at kafka.Kafka$.main(Kafka.scala:82)
        at kafka.Kafka.main(Kafka.scala)
```



## Spring kafka

测试代码：[主函数]()，[Consumer]()，[Producer]()

### 引入POM

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
	<optional>true</optional>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter</artifactId>
</dependency>
```

### 配置yml

```yaml
server:
  port: 9999
spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9011,127.0.0.1:9012,127.0.0.1:9013 # 设置为自己的kafka集群
    consumer:
      group-id: test-consumer-group
      auto-offset-reset: earliest
      enable-auto-commit: true
      auto-commit-interval: 100
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      retries: 0
      batch-size: 16384
      buffer-memory: 33554432
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

### ConsumerTest

```java
@Component
public class KafkaConsumer {
    @KafkaListener(topics = {"topic-test"})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            System.out.println(record + "=>" + message);
        }
    }
}
```

### ProducerTest

```java
@RunWith(SpringRunner.class)

@SpringBootTest()
public class ProducerTest {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void produce() {
        kafkaProducer.send();
    }
}
```
