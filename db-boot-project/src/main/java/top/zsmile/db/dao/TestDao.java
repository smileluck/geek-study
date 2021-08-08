package top.zsmile.db.dao;

import org.apache.ibatis.annotations.Mapper;
import top.zsmile.db.entity.TTest;

import java.util.List;

@Mapper
public interface TestDao {

    List<TTest> selectList();

    void insertNewId(long id);
}
