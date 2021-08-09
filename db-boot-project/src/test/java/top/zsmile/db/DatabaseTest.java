package top.zsmile.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import top.zsmile.db.entity.TTest;
import top.zsmile.db.service.TestService;
import top.zsmile.db.service.impl.TestServiceImpl;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
//@MapperScan(basePackages = "top.zsmile.db.mapper")
@TestPropertySource("classpath:application.yml")
public class DatabaseTest {

    @Autowired
    private TestService testService;

    @Test
    public void testSelect() {
//        List<TTest> tTests = testService.selectList();
//        System.out.println(tTests);

        testService.insertNewId(11);
    }
}
