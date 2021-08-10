package top.zsmile.db.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zsmile.db.datasource.annotation.ReadOnly;
import top.zsmile.db.mapper.TestDao;
import top.zsmile.db.entity.TbTest;
import top.zsmile.db.service.TestService;

import java.util.List;

@Service("testService")
public class TestServiceImpl extends ServiceImpl<TestDao, TbTest> implements TestService {
    @Autowired
    private TestDao testDao;

    @Override
    @ReadOnly
    public List<TbTest> selectList() {
        return testDao.selectList();
    }

    @Override
    public int insertNewId(long id) {
        TbTest tbTest = new TbTest();
        tbTest.setId(id);
        return testDao.insert(tbTest);


//         testDao.insertNewId(id);
//         return  1;
    }

}
