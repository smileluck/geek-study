package top.zsmile.db;

import org.apache.ibatis.annotations.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.zsmile.db.entity.OrderEntity;
import top.zsmile.db.service.OrderService;

import java.util.List;

@SpringBootTest()
@RunWith(SpringRunner.class)
@MapperScan(basePackages = "top.zsmile.db.dao")
public class DatabaseTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void test() {
        List<OrderEntity> orderEntities = orderService.selectList();
        System.out.println(orderEntities);

//        for (int i = 0; i < 32; i++) {
//            orderService.insertOrder(new OrderEntity(i, i));
//        }

        orderEntities = orderService.selectList();
        System.out.println(orderEntities);

        orderService.deleteOrderById(10L);

        orderEntities = orderService.selectList();
        System.out.println(orderEntities);

        orderService.updateOrder(new OrderEntity(1, 2));

        orderEntities = orderService.selectList();
        System.out.println(orderEntities);

    }
}
