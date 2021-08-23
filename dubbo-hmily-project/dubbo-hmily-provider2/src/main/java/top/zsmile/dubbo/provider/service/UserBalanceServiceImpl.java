package top.zsmile.dubbo.provider.service;

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


@DubboService(version = "2.0.0", tag = "bankB", weight = 100)
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceDao, UserBalanceEntity> implements UserBalanceService {

    private final Long userId = 2L;

    @Override
    @HmilyTCC(confirmMethod = "tradeConfirm", cancelMethod = "tradeCancel")
    public boolean trade(BigDecimal money) throws Exception {
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        BigDecimal rmbDollar = money.multiply(new BigDecimal(7));
        if (rmbAccount.getMoney().intValue() >= rmbDollar.intValue()) {
            update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("money", rmbAccount.getMoney().subtract(rmbDollar)).set("frozen_money", rmbAccount.getFrozenMoney().add(rmbDollar)));
            return true;
        } else {
//            return false;
            throw new Exception("no money");
        }
    }

    @Override
    public void tradeConfirm(BigDecimal money) {
        System.out.println("confirm trade bankB");
        BigDecimal rmbDollar = money.multiply(new BigDecimal(7));
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("frozen_money", dollarAccount.getFrozenMoney().subtract(rmbDollar)));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", rmbAccount.getMoney().add(money)));
    }

    @Override
    public void tradeCancel(BigDecimal money) {
        System.out.println("cancel trade bankB");
        BigDecimal rmbDollar = money.multiply(new BigDecimal(7));
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("money", rmbAccount.getMoney().add(rmbDollar)).set("frozen_money", dollarAccount.getFrozenMoney().subtract(rmbDollar)));
//        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", rmbAccount.getMoney().add(money)));
    }
}
