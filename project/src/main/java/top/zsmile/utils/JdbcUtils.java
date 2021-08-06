package top.zsmile.utils;

import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Data
public class JdbcUtils {

    private String url;
    private String username;
    private String password;

    public JdbcUtils(String url, String username, String password) {
        this.password = password;
        this.username = username;
        this.url = url;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
