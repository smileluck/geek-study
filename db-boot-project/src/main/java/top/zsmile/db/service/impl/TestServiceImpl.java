package top.zsmile.db.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zsmile.db.mapper.TestDao;
import top.zsmile.db.entity.TTest;
import top.zsmile.db.service.TestService;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestDao testDao;

    @Override
//    @ReadOnly
    public List<TTest> selectList() {
        return testDao.selectList();
    }

    @Override
    public int insertNewId(long id) {
        return testDao.insertNewId(id);
    }
}
