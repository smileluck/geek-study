package top.zsmile.db.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import top.zsmile.db.dao.TestDao;
import top.zsmile.db.datasource.annotation.ReadOnly;
import top.zsmile.db.entity.TTest;
import top.zsmile.db.service.TestService;

import java.util.List;

public class TestServiceImpl implements TestService {
    @Autowired
    private TestDao testDao;

    @Override
    @ReadOnly
    public List<TTest> selectList() {
        return testDao.selectList();
    }

    @Override
    public void insertNewId(long id) {
        testDao.insertNewId(id);
    }
}
