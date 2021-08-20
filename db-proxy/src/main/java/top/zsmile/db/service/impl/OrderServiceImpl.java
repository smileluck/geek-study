package top.zsmile.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zsmile.db.dao.OrderDao;
import top.zsmile.db.entity.OrderEntity;
import top.zsmile.db.service.OrderService;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public List<OrderEntity> selectList() {
        return orderDao.selectList();
    }

    @Override
    public void insertOrder(OrderEntity orderEntity) {
        orderDao.insertOrder(orderEntity);
    }

    @Override
    public void deleteOrderById(Long id) {
        orderDao.deleteOrderById(id);
    }

    @Override
    public void updateOrder(OrderEntity orderEntity) {
        orderDao.updateOrder(orderEntity);
    }
}
