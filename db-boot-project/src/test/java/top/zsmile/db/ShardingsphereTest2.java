package top.zsmile.db;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.ReplicaQueryRuleConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.rule.ReplicaQueryDataSourceRuleConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SpringBootTest()
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ShardingsphereTest2 {


    @Autowired
    private Environment environment;

    final String prefix = "spring.shardingsphere.dataSources";


    @Test
    public void testSelect() throws InterruptedException, IOException, SQLException {

        String[] dsname = environment.getProperty(prefix + ".names").split(",");
        System.out.println(dsname);

        Map<String, DataSource> dataSourceMap = createDataSourceMap(dsname);

        ReplicaQueryDataSourceRuleConfiguration replicaQueryDataSourceRuleConfiguration = new ReplicaQueryDataSourceRuleConfiguration("ds", dsname[0], Arrays.asList(dsname[1], dsname[2]), "ROUND_ROBIN");

        Map<String, ShardingSphereAlgorithmConfiguration> loadBalanceMaps = new HashMap<>
                (1);
        loadBalanceMaps.put("roundRobin", new ShardingSphereAlgorithmConfiguration("ROUND_ROBIN", new Properties()));

        ReplicaQueryRuleConfiguration replicaQueryRuleConfiguration = new ReplicaQueryRuleConfiguration(Arrays.asList(replicaQueryDataSourceRuleConfiguration), loadBalanceMaps);

        Properties properties = new Properties();
        properties.setProperty("sql.show", "true");
        DataSource dataSource = ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, Arrays.asList(replicaQueryRuleConfiguration), properties);


        Connection connection = dataSource.getConnection();
        System.out.println("第一次查询------");
        PreparedStatement preparedStatement = connection.prepareStatement("select * from tb_test");
        try (ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                System.out.println("id:" + rs.getLong("id"));
            }
        }

        System.out.println("插入数据----");
        PreparedStatement preparedStatement1 = connection.prepareStatement("insert into tb_test value(?)");
        preparedStatement1.setLong(1, 123);
        preparedStatement1.executeUpdate();

        System.out.println("第二次查询------");
        preparedStatement = connection.prepareStatement("select * from tb_test");
        try (ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                System.out.println("id:" + rs.getLong("id"));
            }
        }

    }

    public Map<String, DataSource> createDataSourceMap(String[] dsName) {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (String name : dsName) {
            DataSource dataSource = createDataSource(prefix + "." + name);
            dataSourceMap.put(name, dataSource);
        }
        return dataSourceMap;
    }


    private DataSource createDataSource(String prefix) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(environment.getProperty(prefix + ".driver-class-name"));
        dataSource.setJdbcUrl(environment.getProperty(prefix + ".jdbc-url"));
        dataSource.setUsername(environment.getProperty(prefix + ".username"));
        dataSource.setPassword(environment.getProperty(prefix + ".password"));
        return dataSource;
    }
}
