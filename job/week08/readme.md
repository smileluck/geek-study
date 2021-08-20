[toc]

---

# 题目

**1.（选做）**分析前面作业设计的表，是否可以做垂直拆分。
**2.（必做）**设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。
**3.（选做）**模拟 1000 万的订单单表数据，迁移到上面作业 2 的分库分表中。
**4.（选做）**重新搭建一套 4 个库各 64 个表的分库分表，将作业 2 中的数据迁移到新分库。

**5.（选做）**列举常见的分布式事务，简单分析其使用场景和优缺点。
**6.（必做）**基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github。
**7.（选做）**基于 ShardingSphere narayana XA 实现一个简单的分布式事务 demo。
**8.（选做）**基于 seata 框架实现 TCC 或 AT 模式的分布式事务 demo。
**9.（选做☆）**设计实现一个简单的 XA 分布式事务框架 demo，只需要能管理和调用 2 个 MySQL 的本地事务即可，不需要考虑全局事务的持久化和恢复、高可用等。
**10.（选做☆）**设计实现一个 TCC 分布式事务框架的简单 Demo，需要实现事务管理器，不需要实现全局事务的持久化和恢复、高可用等。
**11.（选做☆）**设计实现一个 AT 分布式事务框架的简单 Demo，仅需要支持根据主键 id 进行的单个删改操作的 SQL 或插入操作的事务。



# 作业二

测试代码：[代码地址](https://github.com/smileluck/geek-study/tree/test/db-proxy/src/main/java/top/zsmile/db/DatabaseTest.java)

1. 先在本地创建2个数据库

   ```sql
   create database geek_ds_0 CHARSET=utf8mb4;
   create database geek_ds_1 CHARSET=utf8mb4;
   ```

2. 配置shardingSphere Proxy 

   - 下载[Shardingsphere-Proxy](https://www.apache.org/dyn/closer.cgi/shardingsphere/5.0.0-beta/apache-shardingsphere-5.0.0-beta-shardingsphere-proxy-bin.tar.gz)，使用bindzip解压，不然会出现jar丢失，程序无法运行的问题

     ![image-20210814181719888](C:\Users\Admin\AppData\Roaming\Typora\typora-user-images\image-20210814181719888.png)

   - 因为这里使用的是mysql，所以需要下载[mysql包](https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.49/mysql-connector-java-5.1.49.jar)，下载后，将jar包放入ext-lib文件下。

   - 更改server.yaml和cofing-sharding.yaml文件

   ```yaml
   rules:
     - !AUTHORITY
       users:
         - root@%:root
         - sharding@:sharding
       provider:
         type: NATIVE
   
   props:
     max-connections-size-per-query: 1
     executor-size: 16  # Infinite by default.
     proxy-frontend-flush-threshold: 128  # The default value is 128.
       # LOCAL: Proxy will run with LOCAL transaction.
       # XA: Proxy will run with XA transaction.
       # BASE: Proxy will run with B.A.S.E transaction.
     proxy-transaction-type: LOCAL
     xa-transaction-manager-type: Atomikos
     proxy-opentracing-enabled: false
     proxy-hint-enabled: false
     sql-show: true
     check-table-metadata-enabled: false
     lock-wait-timeout-milliseconds: 50000 # The maximum time to wait for a lock
   ```
   
   ```yaml
   
   schemaName: sharding_db
   
   dataSources:
     ds_0:
       url: jdbc:mysql://127.0.0.1:3306/geek_ds_1?serverTimezone=UTC&useSSL=false
       username: root
       password: root
       connectionTimeoutMilliseconds: 30000
       idleTimeoutMilliseconds: 60000
       maxLifetimeMilliseconds: 1800000
       maxPoolSize: 50
       minPoolSize: 1
       maintenanceIntervalMilliseconds: 30000
     ds_1:
       url: jdbc:mysql://127.0.0.1:3306/geek_ds_0?serverTimezone=UTC&useSSL=false
       username: root
       password: root
       connectionTimeoutMilliseconds: 30000
       idleTimeoutMilliseconds: 60000
       maxLifetimeMilliseconds: 1800000
       maxPoolSize: 50
       minPoolSize: 1
       maintenanceIntervalMilliseconds: 30000
   #
   rules:
   - !SHARDING
     tables:
       tb_order:
         actualDataNodes: ds_${0..1}.tb_order_${0..15}
         tableStrategy:
           standard:
             shardingColumn: id
             shardingAlgorithmName: tb_order_inline
     bindingTables:
       - tb_order
     defaultDatabaseStrategy:
       standard:
         shardingColumn: user_id
         shardingAlgorithmName: database_inline
     defaultTableStrategy:
       none:
   #
     shardingAlgorithms:
       database_inline:
         type: INLINE
         props:
           algorithm-expression: ds_${user_id % 2}
       tb_order_inline:
         type: INLINE
         props:
           algorithm-expression: tb_order_${id % 16}
   
   ```
   
3. 启动服务

   ```shell
   ./bin/start.bat 3307
   ```



4. 使用mysql连接我们的sharding-proxy，并创建数据库

   ```sql
   mysql -P3307 -uroot -proot
   
   CREATE TABLE IF NOT EXISTS `tb_order` (
       `id` int(11) NOT NULL,
       `user_id` int(11) NOT NULL,
       PRIMARY KEY (`id`)
   ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
   ```

5. 测试代码

   ```java
   package top.zsmile.db;
   
   import org.apache.ibatis.annotations.Mapper;
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.mybatis.spring.annotation.MapperScan;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.context.SpringBootTest;
   import org.springframework.test.context.junit4.SpringRunner;
   import top.zsmile.db.entity.OrderEntity;
   import top.zsmile.db.service.OrderService;
   
   import java.util.List;
   
   @SpringBootTest()
   @RunWith(SpringRunner.class)
   @MapperScan(basePackages = "top.zsmile.db.dao")
   public class DatabaseTest {
   
       @Autowired
       private OrderService orderService;
   
       @Test
       public void test() {
           List<OrderEntity> orderEntities = orderService.selectList();
           System.out.println(orderEntities);
   
           for (int i = 0; i < 32; i++) {            
           orderService.insertOrder(new OrderEntity(i, i));
           }
   
           orderEntities = orderService.selectList();
           System.out.println(orderEntities);
   
           orderService.deleteOrderById(10L);
   
           orderEntities = orderService.selectList();
           System.out.println(orderEntities);
   
           orderService.updateOrder(new OrderEntity(1, 2));
   
           orderEntities = orderService.selectList();
           System.out.println(orderEntities);
   
       }
   }
   ```

# 作业六

测试代码：[代码地址](https://github.com/smileluck/geek-study/tree/test/db-proxy/src/main/java/top/zsmile/db/XaTest.java)

1. 先在本地创建2个数据库

   ```sql
   create database geek_ds_0 CHARSET=utf8mb4;
   create database geek_ds_1 CHARSET=utf8mb4;
   ```

2. 配置pom文件

   ```xml
   
           <dependency>
               <groupId>com.zaxxer</groupId>
               <artifactId>HikariCP</artifactId>
               <version>4.0.3</version>
           </dependency>
           <dependency>
               <groupId>org.apache.shardingsphere</groupId>
               <artifactId>shardingsphere-jdbc-core</artifactId>
               <version>5.0.0-alpha</version>
           </dependency>
   
           <!-- 使用 XA 事务时，需要引入此模块 -->
           <dependency>
               <groupId>org.apache.shardingsphere</groupId>
               <artifactId>shardingsphere-transaction-xa-core</artifactId>
               <version>5.0.0-alpha</version>
           </dependency>
   ```

3. 配置shardingshpere-jdbc.yaml

   ```yaml
   # 配置真实数据源
   dataSources:
     # 配置第 1 个数据源
     ds_0: !!com.zaxxer.hikari.HikariDataSource
       driverClassName: com.mysql.jdbc.Driver
       jdbcUrl: jdbc:mysql://localhost:3306/geek_ds_0
       username: root
       password: root
     # 配置第 2 个数据源
     ds_1: !!com.zaxxer.hikari.HikariDataSource
       driverClassName: com.mysql.jdbc.Driver
       jdbcUrl: jdbc:mysql://localhost:3306/geek_ds_1
       username: root
       password: root
   
   rules:
     - !SHARDING
       tables:
         # 配置 t_order 表规则
         tb_order:
           actualDataNodes: ds_${0..1}.tb_order_${0..15}
           # 配置分库策略
           databaseStrategy:
             standard:
               shardingColumn: user_id
               shardingAlgorithmName: database_inline
           # 配置分表策略
           tableStrategy:
             standard:
               shardingColumn: id
               shardingAlgorithmName: table_inline
       # 配置分片算法
       shardingAlgorithms:
         database_inline:
           type: INLINE
           props:
             algorithm-expression: ds_${user_id % 2}
             allow-range-query-with-inline-sharding: true
         table_inline:
           type: INLINE
           props:
             algorithm-expression: tb_order_${id % 16}
             allow-range-query-with-inline-sharding: true
   props:
     sql-show: true
   
   ```

4. 编写测试代码

   ```java
   package top.zsmile.db;
   
   
   import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
   import org.apache.shardingsphere.transaction.core.TransactionType;
   import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
   import org.junit.Test;
   
   import javax.sql.DataSource;
   import java.io.File;
   import java.io.IOException;
   import java.net.URL;
   import java.sql.Connection;
   import java.sql.PreparedStatement;
   import java.sql.ResultSet;
   import java.sql.SQLException;
   
   public class XaTest {
       
   
       @Test
       public void test() throws IOException, SQLException {
   
           URL resource = XaTest.class.getClassLoader().getResource("shardingshpere-xa-atomiks.yaml");
           File file = new File(resource.getFile());
           DataSource dataSource = YamlShardingSphereDataSourceFactory.createDataSource(file);
           Connection connection = dataSource.getConnection();
   
   
           PreparedStatement preparedStatement = connection.prepareStatement("delete from tb_order");
           preparedStatement.executeUpdate();
   
   
           preparedStatement = connection.prepareStatement("select * from tb_order");
   
           ResultSet resultSet = preparedStatement.executeQuery();
           while (resultSet.next()) {
               System.out.println("id：" + resultSet.getLong("id") + "，userId：" + resultSet.getLong("user_id"));
           }
   
           TransactionTypeHolder.set(TransactionType.XA); // 支持 TransactionType.LOCAL, TransactionType.XA, TransactionType.BASE
           connection.setAutoCommit(false);
   
           try {
               PreparedStatement ps = connection.prepareStatement("INSERT INTO tb_order (id, user_id) VALUES (?, ?)");
               for (int i = 50; i < 60; i++) {
                   ps.setInt(1, i);
                   ps.setInt(2, i);
                   ps.executeUpdate();
               }
   
               connection.commit();
           } catch (Exception e) {
               e.printStackTrace();
               connection.rollback();
           }
           System.out.println("test xa 1 finish ,start search");
   
   
           resultSet = connection.prepareStatement("select * from tb_order where id >= 50").executeQuery();
           connection.commit();
           while (resultSet.next()) {
               System.out.println("id：" + resultSet.getLong("id") + "，userId：" + resultSet.getLong("user_id"));
           }
   
           System.out.println("finish search,start twice xa");
   
           // 这里会出现59 主键重复，这时就会报错回滚，后面的查询语句将会看不到60以上的数据
           try {
               PreparedStatement ps = connection.prepareStatement("INSERT INTO tb_order (id, user_id) VALUES (?, ?)");
               for (int i = 65; i > 50; i--) {
                   ps.setInt(1, i + 3);
                   ps.setInt(2, i + 3);
                   ps.executeUpdate();
               }
   
               connection.commit();
           } catch (Exception e) {
               e.printStackTrace();
               connection.rollback();
           }
           System.out.println("finish twice xa,start search");
   
   
           preparedStatement = connection.prepareStatement("select * from tb_order");
   
           resultSet = preparedStatement.executeQuery();
           while (resultSet.next()) {
               System.out.println("id：" + resultSet.getLong("id") + "，userId：" + resultSet.getLong("user_id"));
           }
           System.out.println("finish search,over");
           connection.close();
       }
   
   }
   
   ```

   