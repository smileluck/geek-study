package top.zsmile.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.zsmile.db.entity.TbTest;

import java.util.List;

@Mapper
public interface TestDao extends BaseMapper<TbTest> {

    List<TbTest> selectList();

    void insertNewId(@Param("id") Long id);
}
