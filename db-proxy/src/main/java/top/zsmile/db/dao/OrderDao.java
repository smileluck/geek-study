package top.zsmile.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.aspectj.weaver.ast.Or;
import top.zsmile.db.entity.OrderEntity;

import java.util.List;

@Mapper
public interface OrderDao {

    List<OrderEntity> selectList();

    void insertOrder(@Param("info") OrderEntity orderEntity);


    void deleteOrderById(Long id);

    void updateOrder(@Param("info") OrderEntity orderEntity);
}
