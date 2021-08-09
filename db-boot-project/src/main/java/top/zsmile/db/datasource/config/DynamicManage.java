package top.zsmile.db.datasource.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class DynamicManage {
    @Resource(name = "primaryDataSource")
    private DataSource primaryDataSource;

    @Resource(name = "slave1DataSource")
    private DataSource slave1DataSource;

    @Resource(name = "slaveDataSource")
    private DataSource slaveDataSource;

    public DataSource getPrimaryDataSource(){
        return this.primaryDataSource;
    }

    private int slaveIndex = 0;

    public String getSlaveDataSource(){
        Random random = new Random();
        if(slaveIndex%2==0){
            slaveIndex++;
            return "slave";
        }else{
            slaveIndex++;
            return "slave1";
        }
    }


    /**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     * @return
     */
    @Primary
    @Bean(name = "dynamicDataSource")
    public DataSource dataSource(@Autowired @Qualifier("primaryDataSource") DataSource primary, @Autowired @Qualifier("slave1DataSource") DataSource slave1
            , @Autowired @Qualifier("slaveDataSource") DataSource slave) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 默认数据源
        dynamicDataSource.setDefaultTargetDataSource(primary);

        // 配置多数据源
        Map<Object, Object> dsMap = new HashMap<Object, Object>();
        dsMap.put("master", primary);
        dsMap.put("slave", slave);
        dsMap.put("slave1", slave1);
        dynamicDataSource.setTargetDataSources(dsMap);
        return dynamicDataSource;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory db1SqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }
}
