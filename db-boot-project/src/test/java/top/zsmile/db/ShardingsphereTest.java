package top.zsmile.db;

import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import top.zsmile.db.entity.TbTest;
import top.zsmile.db.service.TestService;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest()
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ShardingsphereTest {


    @Autowired
    private Environment environment;


    @Test
    public void testSelect() throws InterruptedException, IOException, SQLException {

//        String property = environment.getProperty("spring.shardingsphere.dataSources");
//
//        DataSource dataSource = ShardingSphereDataSourceFactory.
//                createDataSource(dataSourceMap, configurations, properties);

        URL resource = ShardingsphereTest.class.getClassLoader().getResource("application-shardingsphere.yml");
        File file = new File(resource.getFile());

        DataSource dataSource = YamlShardingSphereDataSourceFactory.createDataSource(file);

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from tb_test");
        try (ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                System.out.println("id:" + rs.getLong("id"));
            }
        }
    }
}
