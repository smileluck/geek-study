package top.zsmile.dubbo.provider;

import org.dromara.hmily.spring.annotation.RefererAnnotationBeanPostProcessor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

//@SpringBootApplication
//@MapperScan(basePackages = "top.zsmile.dubbo.provider")
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class DubboServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboServerApplication.class, args);
    }

    @Bean
    public BeanPostProcessor refererAnnotationBeanPostProcessor() {
        return new RefererAnnotationBeanPostProcessor();
    }
}
