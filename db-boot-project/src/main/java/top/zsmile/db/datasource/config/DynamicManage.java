package top.zsmile.db.datasource.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
//    @Resource(name = "primaryDataSource")
//    private DataSource primaryDataSource;
//
//    @Resource(name = "slave1DataSource")
//    private DataSource slave1DataSource;
//
//    @Resource(name = "slaveDataSource")
//    private DataSource slaveDataSource;
//
//    public DataSource getPrimaryDataSource(){
//        return this.primaryDataSource;
//    }

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





}
