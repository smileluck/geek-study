package top.zsmile.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.zsmile.spring.entity.Klass;
import top.zsmile.spring.entity.School;
import top.zsmile.spring.entity.Student;
import top.zsmile.spring.prop.SpringBootPropertiseConfiguration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(SpringBootPropertiseConfiguration.class)
@ConditionalOnProperty(prefix = "zsmile.springboot", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = {"top.zsmile.spring.entity", "top.zsmile.spring.prop"})
public class MyAutoConfiguration {

    @Autowired
    private SpringBootPropertiseConfiguration springBootPropertiseConfiguration;

    @Bean(name = "student100")
    public Student student() {
        Student student = springBootPropertiseConfiguration.getStudent();
        return student;
    }

    @Bean
    public Klass klass() {
        Klass klass = new Klass();
        List<Student> students = new ArrayList<>();
        students.add(springBootPropertiseConfiguration.getStudent());
        klass.setStudents(students);
        return klass;
    }

    @Bean
    public School school() {
        return new School();
    }
}
