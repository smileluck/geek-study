package top.zsmile.spring;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import top.zsmile.spring.entity.Klass;
import top.zsmile.spring.entity.School;
import top.zsmile.spring.entity.Student;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {top.zsmile.spring.MyAutoConfiguration.class})
@TestPropertySource("classpath:application.yml")
public class SpringTest {

    @Autowired
    private School school;

    @Resource(name = "student100")
    private Student student;

    @Autowired
    private Klass klass;

    @Test
    public void test() {
        System.out.println("school：" + school.getClass1());
        System.out.println("school.student：" + school.getStudent100());

        System.out.println("klass：" + klass.getStudents());
        klass.dong();

        System.out.println("student：" + student.info());
    }
}
