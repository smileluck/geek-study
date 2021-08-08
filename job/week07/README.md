[toc]

---

# 题目

**1.（选做）**用今天课上学习的知识，分析自己系统的 SQL 和表结构
**2.（必做）**按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率
**3.（选做）**按自己设计的表结构，插入 1000 万订单模拟数据，测试不同方式的插入效
**4.（选做）**使用不同的索引或组合，测试不同方式查询效率
**5.（选做）**调整测试数据，使得数据尽量均匀，模拟 1 年时间内的交易，计算一年的销售报表：销售总额，订单数，客单价，每月销售量，前十的商品等等（可以自己设计更多指标）
**6.（选做）**尝试自己做一个 ID 生成器（可以模拟 Seq 或 Snowflake）
**7.（选做）**尝试实现或改造一个非精确分页的程序

**8.（选做）**配置一遍异步复制，半同步复制、组复制
**9.（必做）**读写分离 - 动态切换数据源版本 1.0
**10.（必做）**读写分离 - 数据库框架版本 2.0
**11.（选做）**读写分离 - 数据库中间件版本 3.0
**12.（选做）**配置 MHA，模拟 master 宕机
**13.（选做）**配置 MGR，模拟 master 宕机
**14.（选做）**配置 Orchestrator，模拟 master 宕机，演练 UI 调整拓扑结构





# 第二题

## jdbc.preparedStatement

[测试地址](https://github.com/smileluck/geek-study/tree/main/project/src/main/java/top/zsmile/sql/mock/InsertDataMock.java)

========添加1000000订单和商品快照，每次执行添加1000个======
开始时间：1628239152426
结束时间：1628239564679
总耗时：412253

```java
package top.zsmile.sql.mock;


import top.zsmile.utils.JdbcUtils;
import top.zsmile.utils.SequenceUtils;

import java.sql.*;
import java.util.Random;

public class InsertDataMock {

    public static void main(String[] args) {
        InsertDataMock insertDataMock = new InsertDataMock();
        JdbcUtils jdbcUtils = new JdbcUtils("jdbc:mysql://127.0.0.1:3306/geek_shop", "root", "root");

        Connection connection = jdbcUtils.getConnection();
        if (connection != null) {
            insertDataMock.insertOrderByPreparedStatement(connection);
        }
        jdbcUtils.closeConnection(connection);
    }


    public void insertUserByPreparedStatement(Connection connection) {
        long startTime = System.currentTimeMillis();


        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into tb_user(username,create_time) values(?,current_time)");
            for (int i = 1; i <= 100; i++) {
                preparedStatement.setObject(1, "user_" + System.currentTimeMillis());

                //保存sql
                preparedStatement.addBatch();

                //整除执行
                if (i % 100 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            connection.commit();


            long endTime = System.currentTimeMillis();

            System.out.println("========添加100000用户，每次执行添加1000个======");
            System.out.println("开始时间：" + startTime);
            System.out.println("结束时间：" + endTime);
            System.out.println("总耗时：" + (endTime - startTime));


        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public void insertOrderByPreparedStatement(Connection connection) {
        long startTime = System.currentTimeMillis();


        try {

            ResultSet goodSet = connection.prepareCall("select * from tb_good", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();


            SequenceUtils orderGoodSeq = new SequenceUtils("order-good", 1000);
            SequenceUtils orderSeq = new SequenceUtils("order", 1000);

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement("insert into tb_order" +
                    "(id,user_id,good_id,order_good_id,price,real_price,status,create_time) " +
                    "values(?,?,?,?,?,?,?,current_time)");

            PreparedStatement preparedStatement2 = connection.prepareStatement("insert into tb_order_good" +
                    "(id,order_id,good_id,`name`,price,create_time) " +
                    "values(?,?,?,?,?,current_time)");

            Random random = new Random();
            for (int i = 1; i <= 1000000; i++) {

                long orderGoodId = orderGoodSeq.getNext();
                long orderId = orderSeq.getNext();
                long userId = random.nextInt(100104);

                System.out.println(orderId + "-" + orderGoodId + "-" + userId);

                preparedStatement.setObject(1, orderId);
                preparedStatement.setObject(2, userId); //100104为数据库最后一个用户id
                while (true) {
                    if (goodSet.next()) {

                        preparedStatement.setObject(3, goodSet.getLong("id"));
                        preparedStatement.setObject(4, orderGoodId);
                        preparedStatement.setObject(5, goodSet.getLong("price"));
                        preparedStatement.setObject(6, goodSet.getLong("price") * 0.996);
                        int status = random.nextInt(3) + 1;
                        preparedStatement.setObject(7, status);


                        preparedStatement2.setObject(1, orderGoodId);
                        preparedStatement2.setObject(2, orderId);
                        preparedStatement2.setObject(3, goodSet.getLong("id"));
                        preparedStatement2.setObject(4, goodSet.getString("name"));
                        preparedStatement2.setObject(5, goodSet.getLong("price"));

                        preparedStatement2.addBatch();
                        break;
                    } else {
                        goodSet.first();
                    }
                }


                //保存sql
                preparedStatement.addBatch();

                //整除执行
                if (i % 1000 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                    preparedStatement2.executeBatch();
                    preparedStatement2.clearBatch();
                }
            }
            connection.commit();


            long endTime = System.currentTimeMillis();

            System.out.println("========添加1000000订单和商品快照，每次执行添加1000个======");
            System.out.println("开始时间：" + startTime);
            System.out.println("结束时间：" + endTime);
            System.out.println("总耗时：" + (endTime - startTime));


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
```

## load.sql file 

[文件地址](https://github.com/smileluck/geek-study/tree/main/job/week07/tb_order & tb_order_good.sql)

![image-20210806165638801](image-20210806165638801.png)









通过导入.sql文件，花费时间2分10秒06。

# 第六题

[测试地址](https://github.com/smileluck/geek-study/tree/main/project/src/main/java/top/zsmile/utils/SequenceUtils.java)

数据表

```sql
CREATE TABLE `tb_sequence` (
  `seq_name` varchar(50) NOT NULL COMMENT '系列名称',
  `current_val` int(11) NOT NULL COMMENT '当前值',
  `increment_val` int(11) NOT NULL COMMENT '步长值'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
```

java代码

```java
package top.zsmile.utils;

import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class SequenceUtils {
    private long maxKey;        //当前Sequence载体的最大值
    private long minKey;        //当前Sequence载体的最小值
    private long nextKey;       //下一个Sequence值
    private int poolSize = 100;       //Sequence值缓存大小
    private String keyName;     //Sequence的名称

    private final int initNum = 1; //初始化当前值为0

    public SequenceUtils(String keyName) {
        this.keyName = keyName;
        searchSequence();
    }

    public SequenceUtils(String keyName, int poolSize) {
        this.keyName = keyName;
        this.poolSize = poolSize;
        searchSequence();
    }

    public synchronized long getNext() {
        if (this.nextKey >= this.maxKey) {
            searchSequence();
        }
        return this.nextKey++;
    }

    public void searchSequence() {
        JdbcUtils jdbcUtils = new JdbcUtils("jdbc:mysql://127.0.0.1:3306/geek_shop", "root", "root");

        Connection connection = jdbcUtils.getConnection();
        try {
            if (connection != null) {

                PreparedStatement preparedStatement = connection.prepareStatement("select * from tb_sequence where seq_name = ?");
                preparedStatement.setString(1, this.keyName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    long currentVal = resultSet.getLong("current_val");
                    long incrementVal = resultSet.getLong("increment_val");
                    this.minKey = currentVal;
                    this.nextKey = this.minKey;
                    this.maxKey = this.minKey + incrementVal;
                    PreparedStatement updateStatement = connection.prepareStatement("update tb_sequence set current_val = ? where seq_name = ?");
                    updateStatement.setLong(1, this.maxKey);
                    updateStatement.setString(2, this.keyName);
                    updateStatement.executeUpdate();
                    updateStatement.close();
                } else {
                    initSequence(connection);
                }

                preparedStatement.close();

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        jdbcUtils.closeConnection(connection);
    }

    public void initSequence(Connection connection) {
        try {
            this.maxKey = this.initNum + this.poolSize;
            this.minKey = this.initNum;
            this.nextKey = this.minKey;
            PreparedStatement preparedStatement = connection.prepareStatement("insert into tb_sequence(seq_name,current_val,increment_val) value(?,?,?)");
            preparedStatement.setString(1, this.keyName);
            preparedStatement.setLong(2, this.maxKey);
            preparedStatement.setInt(3, this.poolSize);
            preparedStatement.executeUpdate();
            preparedStatement.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SequenceUtils sequenceUtils = new SequenceUtils("order-good");
        int i = 1;
        while (i < 3) {
            long next = sequenceUtils.getNext();
            System.out.println(next);
            if (next == 0) i++;

        }
    }
}
```
