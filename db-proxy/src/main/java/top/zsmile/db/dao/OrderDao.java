package top.zsmile.db.dao;

import org.apache.ibatis.annotations.Mapper;
import top.zsmile.db.entity.OrderEntity;

import java.util.List;

@Mapper
public class OrderDao {

    List<OrderEntity> selectList();

    public insertOrder(OrderEntity orderEntity);
}
