package top.zsmile.db.service;

import org.springframework.stereotype.Service;
import top.zsmile.db.entity.TTest;

import java.util.List;

@Service
public interface TestService {
    public List<TTest> selectList();

    public void insertNewId(long id);
}
