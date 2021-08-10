package top.zsmile.db.service;

import top.zsmile.db.datasource.annotation.ReadOnly;
import top.zsmile.db.entity.TbTest;

import java.util.List;

public interface TestService  {

     List<TbTest> selectList();

     int insertNewId(long id);
}
