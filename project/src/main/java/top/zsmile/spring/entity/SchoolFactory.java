package top.zsmile.spring.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SchoolFactory {

    @Bean("School22")
    private School1 createSchool() {
        return new School1();
    }
}
