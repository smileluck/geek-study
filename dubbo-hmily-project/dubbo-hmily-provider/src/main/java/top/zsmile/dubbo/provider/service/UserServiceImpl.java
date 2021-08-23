package top.zsmile.dubbo.provider.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import top.zsmile.dubbo.provider.dao.UserDao;
import top.zsmile.dubbo.entity.UserEntity;
import top.zsmile.dubbo.provider.service.UserService;

@DubboService(version = "1.0.0", tag = "bankA", weight = 100)
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
}
