package top.zsmile.sql.jdbc;

import java.sql.*;
import java.util.Random;

public class JdbcTest {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/basis_all", "root", "root");
            createTable(connection);
            int id = insert(connection);
            select(connection);
            update(connection, id);
            select(connection);
            delete(connection, id);
            select(connection);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createTable(Connection connection) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("CREATE TABLE IF NOT EXISTS `test_log`( `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`))");
            boolean execute = callableStatement.execute();
            System.out.println("创建数据库成功 " + execute);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }


    public static int insert(Connection connection) throws SQLException {
        int num = -1;
        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("insert into test_log(name) value(?)");
            callableStatement.setString(1, "name" + new Random().nextInt(1000));

            callableStatement.executeUpdate();

            ResultSet generatedKeys = callableStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                num = generatedKeys.getInt(1);
                System.out.println("insert Data, update column id " + num);
            }
            return num;
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }

    public static void update(Connection connection, int id) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("update test_log set name=? where id = ?");
            callableStatement.setString(1, "update-name" + new Random().nextInt());
            callableStatement.setInt(2, id);

            callableStatement.executeUpdate();
            System.out.println("update Data, id = " + id);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }


    public static void delete(Connection connection, int id) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("delete from test_log where id = ?");
            callableStatement.setInt(1, id);

            callableStatement.executeUpdate();
            System.out.println("delete Data, id = " + id);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }

    public static void select(Connection connection) throws SQLException {
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        try {
            callableStatement = connection.prepareCall("select * from test_log");
            resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("id:" + id + "," + "name:" + name);
            }
            callableStatement.close();
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }


}
