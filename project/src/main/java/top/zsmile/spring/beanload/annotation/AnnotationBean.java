package top.zsmile.spring.beanload.annotation;

import lombok.Data;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import top.zsmile.spring.entity.School1;

import javax.annotation.Resource;

@Component
@Data
@ComponentScan
public class AnnotationBean {

    @Resource(name = "School22")
    private School1 school1;
}
