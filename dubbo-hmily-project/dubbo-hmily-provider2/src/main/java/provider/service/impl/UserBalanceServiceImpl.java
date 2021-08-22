package provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.transaction.annotation.Transactional;
import top.zsmile.dubbo.entity.UserBalanceEntity;
import top.zsmile.dubbo.provider.dao.UserBalanceDao;
import top.zsmile.dubbo.provider.service.UserBalanceService;

import java.math.BigDecimal;


@DubboService(version = "1.0.0", tag = "bankA", weight = 100)
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceDao, UserBalanceEntity> implements UserBalanceService {

    @DubboReference(version = "1.0.0", tag = "bankB") //, url = "dubbo://127.0.0.1:12345")
    private UserBalanceService userBalanceServiceB;

    @Override
    @Transactional
    @HmilyTCC(confirmMethod = "tradeConfirm", cancelMethod = "tradeCancel")
    public void trade(BigDecimal money) throws Exception {
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", 1).eq("cash_type", "dollar"));
        if (dollarAccount.getMoney().intValue() >= money.intValue()) {
            update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", 1).eq("cash_type", "dollar").set("money", dollarAccount.getMoney().subtract(money)).set("frozen_money", dollarAccount.getMoney().add(money)));
            userBalanceServiceB.trade(money);
            update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", 1).eq("cash_type", "dollar").set("frozen_money", dollarAccount.getMoney()));
            UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", 1).eq("cash_type", "rmb"));
            update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", 1).eq("cash_type", "rmb").set("money", rmbAccount.getMoney().add(money)));
        } else {
            throw new Exception("no money");
        }
    }

    @Override
    public void tradeConfirm(BigDecimal money) {
        System.out.println("confirm trade bankA");
    }

    @Override
    public void tradeCancel(BigDecimal money) {
        System.out.println("cancel trade bankA");
    }
}
