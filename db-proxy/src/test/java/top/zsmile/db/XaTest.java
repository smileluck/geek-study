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
