package top.zsmile.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.zsmile.spring.entity.Student;

import java.util.Properties;

@ConfigurationProperties(prefix = "zsmile.springboot")
@Data
public class SpringBootPropertiseConfiguration {

    private Student student;

}
