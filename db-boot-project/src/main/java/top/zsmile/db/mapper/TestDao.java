package top.zsmile.db.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zsmile.db.entity.TTest;

import java.util.List;

@Mapper
public interface TestDao extends BaseMapper<TTest> {

    List<TTest> selectList();

    int insertNewId(long id);
}
