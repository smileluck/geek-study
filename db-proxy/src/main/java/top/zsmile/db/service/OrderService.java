package top.zsmile.db.service;

import org.apache.ibatis.annotations.Param;
import top.zsmile.db.entity.OrderEntity;

import java.util.List;

public interface OrderService {

    List<OrderEntity> selectList();

    void insertOrder(OrderEntity orderEntity);


    void deleteOrderById(Long id);

    void updateOrder(OrderEntity orderEntity);
}
