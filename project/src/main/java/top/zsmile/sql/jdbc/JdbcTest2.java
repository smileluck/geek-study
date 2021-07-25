package top.zsmile.sql.jdbc;

import java.sql.*;
import java.util.Random;

public class JdbcTest2 {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/basis_all", "root", "root");
            connection.setAutoCommit(false);

            createTable(connection);

            insert2Column(connection);
            select(connection);

            insert2ColumnRoll(connection);
            select(connection);

            int id = insert(connection);
            select(connection);

            if (id != -1) {
                update(connection, id);
                select(connection);
                delete(connection, id);
                select(connection);
            }

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

    public static void createTable(Connection connection) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `test_log`( `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`))");

            preparedStatement.execute();
            connection.commit();
            System.out.println("创建数据库成功 ");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void insert2Column(Connection connection) throws SQLException {
        PreparedStatement prepareStatement = null;
        PreparedStatement prepareStatement1 = null;
        try {
            prepareStatement = connection.prepareStatement("insert into test_log(name) value(?)");
            prepareStatement.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement.executeUpdate();

            prepareStatement1 = connection.prepareStatement("insert into test_log(name) value(?)");
            prepareStatement1.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement1.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void insert2ColumnRoll(Connection connection) throws SQLException {
        PreparedStatement prepareStatement = null;
        PreparedStatement prepareStatement1 = null;
        try {
            prepareStatement = connection.prepareStatement("insert into test_log(name) value(?)", Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement.executeUpdate();

            ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println("insert Data, will roll column id " + generatedKeys.getInt(1));
            }

            if (true) {
                System.out.println("手动异常");
                throw new SQLException("error");
            }
            prepareStatement1 = connection.prepareStatement("insert into test_log(name) value(?)");
            prepareStatement1.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement1.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int insert(Connection connection) throws SQLException {
        int num = -1;
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = connection.prepareStatement("insert into test_log(name) value(?)", Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, "name" + new Random().nextInt(1000));

            prepareStatement.executeUpdate();
            connection.commit();
            ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                num = generatedKeys.getInt(1);
                System.out.println("insert Data, update column id " + num);
            }
            return num;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return num;
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void update(Connection connection, int id) throws SQLException {
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = connection.prepareStatement("update test_log set name=? where id = ?");
            prepareStatement.setString(1, "update-name" + new Random().nextInt());
            prepareStatement.setInt(2, id);

            prepareStatement.executeUpdate();
            connection.commit();
            System.out.println("update Data, id = " + id);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void delete(Connection connection, int id) throws SQLException {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("delete from test_log where id = ?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            connection.commit();
            System.out.println("delete Data, id = " + id);

        } catch (
                SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
