package top.zsmile.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import top.zsmile.db.entity.TbTest;
import top.zsmile.db.service.TestService;

import java.util.List;

@SpringBootTest()
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class DatabaseTest {

    @Autowired
    private TestService testService;

    @Test
    public void testSelect() throws InterruptedException {
        List<TbTest> tbTests = testService.selectList();
        System.out.println("查询到数据：" + tbTests);

//        testService.
        int i = testService.insertNewId(12);

        System.out.println("master插入" + i + "条数据");

        Thread.sleep(1000L);

        List<TbTest> tbTests1 = testService.selectList();
        System.out.println("查询到数据：" + tbTests1);
    }
}
