package top.zsmile.db.service;

import org.springframework.stereotype.Service;
import top.zsmile.db.entity.TTest;

import java.util.List;

public interface TestService {
     List<TTest> selectList();

     int insertNewId(long id);
}
