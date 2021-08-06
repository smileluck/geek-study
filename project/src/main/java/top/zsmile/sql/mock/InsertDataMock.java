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
