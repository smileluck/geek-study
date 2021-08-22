package provider.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zsmile.dubbo.entity.UserEntity;

/**
 *
 *
 * @author zsmile
 * @email zsmile@gmail.com
 * @date 2021-08-22 22:07:54
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

}
