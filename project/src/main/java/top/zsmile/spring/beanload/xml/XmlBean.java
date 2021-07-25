package top.zsmile.spring.beanload.xml;

import lombok.Data;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import top.zsmile.spring.entity.School;
import top.zsmile.spring.entity.Student;

import javax.annotation.Resource;

@Configuration
@Data
@ComponentScan
public class XmlBean {

    @Resource(name = "student100")
    private Student student;

    @Resource(name = "student200")
    private Student student200;
}
