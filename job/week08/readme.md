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
   #
   # Licensed to the Apache Software Foundation (ASF) under one or more
   # contributor license agreements.  See the NOTICE file distributed with
   # this work for additional information regarding copyright ownership.
   # The ASF licenses this file to You under the Apache License, Version 2.0
   # (the "License"); you may not use this file except in compliance with
   # the License.  You may obtain a copy of the License at
   #
   #     http://www.apache.org/licenses/LICENSE-2.0
   #
   # Unless required by applicable law or agreed to in writing, software
   # distributed under the License is distributed on an "AS IS" BASIS,
   # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   # See the License for the specific language governing permissions and
   # limitations under the License.
   #
   
   ######################################################################################################
   #
   # If you want to configure governance, authorization and proxy properties, please refer to this file.
   #
   ######################################################################################################
   
   #governance:
   #  name: governance_ds
   #  registryCenter:
   #    type: ZooKeeper
   #    serverLists: localhost:2181
   #    props:
   #      retryIntervalMilliseconds: 500
   #      timeToLiveSeconds: 60
   #      maxRetries: 3
   #      operationTimeoutMilliseconds: 500
   #  overwrite: false
   
   #scaling:
   #  blockQueueSize: 10000
   #  workerThread: 40
   
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
   #
   # Licensed to the Apache Software Foundation (ASF) under one or more
   # contributor license agreements.  See the NOTICE file distributed with
   # this work for additional information regarding copyright ownership.
   # The ASF licenses this file to You under the Apache License, Version 2.0
   # (the "License"); you may not use this file except in compliance with
   # the License.  You may obtain a copy of the License at
   #
   #     http://www.apache.org/licenses/LICENSE-2.0
   #
   # Unless required by applicable law or agreed to in writing, software
   # distributed under the License is distributed on an "AS IS" BASIS,
   # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   # See the License for the specific language governing permissions and
   # limitations under the License.
   #
   
   ######################################################################################################
   #
   # Here you can configure the rules for the proxy.
   # This example is configuration of sharding rule.
   #
   ######################################################################################################
   #
   #schemaName: sharding_db
   #
   #dataSources:
   #  ds_0:
   #    url: jdbc:postgresql://127.0.0.1:5432/demo_ds_0?serverTimezone=UTC&useSSL=false
   #    username: postgres
   #    password: postgres
   #    connectionTimeoutMilliseconds: 30000
   #    idleTimeoutMilliseconds: 60000
   #    maxLifetimeMilliseconds: 1800000
   #    maxPoolSize: 50
   #    minPoolSize: 1
   #    maintenanceIntervalMilliseconds: 30000
   #  ds_1:
   #    url: jdbc:postgresql://127.0.0.1:5432/demo_ds_1?serverTimezone=UTC&useSSL=false
   #    username: postgres
   #    password: postgres
   #    connectionTimeoutMilliseconds: 30000
   #    idleTimeoutMilliseconds: 60000
   #    maxLifetimeMilliseconds: 1800000
   #    maxPoolSize: 50
   #    minPoolSize: 1
   #    maintenanceIntervalMilliseconds: 30000
   #
   #rules:
   #- !SHARDING
   #  tables:
   #    t_order:
   #      actualDataNodes: ds_${0..1}.t_order_${0..1}
   #      tableStrategy:
   #        standard:
   #          shardingColumn: order_id
   #          shardingAlgorithmName: t_order_inline
   #      keyGenerateStrategy:
   #        column: order_id
   #        keyGeneratorName: snowflake
   #    t_order_item:
   #      actualDataNodes: ds_${0..1}.t_order_item_${0..1}
   #      tableStrategy:
   #        standard:
   #          shardingColumn: order_id
   #          shardingAlgorithmName: t_order_item_inline
   #      keyGenerateStrategy:
   #        column: order_item_id
   #        keyGeneratorName: snowflake
   #  bindingTables:
   #    - t_order,t_order_item
   #  defaultDatabaseStrategy:
   #    standard:
   #      shardingColumn: user_id
   #      shardingAlgorithmName: database_inline
   #  defaultTableStrategy:
   #    none:
   #
   #  shardingAlgorithms:
   #    database_inline:
   #      type: INLINE
   #      props:
   #        algorithm-expression: ds_${user_id % 2}
   #    t_order_inline:
   #      type: INLINE
   #      props:
   #        algorithm-expression: t_order_${order_id % 2}
   #    t_order_item_inline:
   #      type: INLINE
   #      props:
   #        algorithm-expression: t_order_item_${order_id % 2}
   #
   #  keyGenerators:
   #    snowflake:
   #      type: SNOWFLAKE
   #      props:
   #        worker-id: 123
   
   ######################################################################################################
   #
   # If you want to connect to MySQL, you should manually copy MySQL driver to lib directory.
   #
   ######################################################################################################
   
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
   #      keyGenerateStrategy:
   #        column: order_id
   #        keyGeneratorName: snowflake
   #    t_order_item:
   #      actualDataNodes: ds_${0..1}.t_order_item_${0..1}
   #      tableStrategy:
   #        standard:
   #          shardingColumn: order_id
   #          shardingAlgorithmName: t_order_item_inline
   #      keyGenerateStrategy:
   #        column: order_item_id
   #        keyGeneratorName: snowflake
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
   #    t_order_item_inline:
   #      type: INLINE
   #      props:
   #        algorithm-expression: t_order_item_${order_id % 2}
   #
   #  keyGenerators:
   #    snowflake:
   #      type: SNOWFLAKE
   #      props:
   #        worker-id: 123
   
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

5. 

# 作业六

