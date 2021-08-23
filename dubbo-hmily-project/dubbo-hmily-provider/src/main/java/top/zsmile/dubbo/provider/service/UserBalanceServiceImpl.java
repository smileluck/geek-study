package top.zsmile.dubbo.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.core.context.HmilyTransactionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.zsmile.dubbo.provider.dao.UserBalanceDao;
import top.zsmile.dubbo.entity.UserBalanceEntity;
import top.zsmile.dubbo.provider.service.UserBalanceService;

import java.math.BigDecimal;


@DubboService(version = "1.0.0", tag = "bankA", weight = 100)
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceDao, UserBalanceEntity> implements UserBalanceService {


    @DubboReference(version = "2.0.0", tag = "bankB") //, url = "dubbo://127.0.0.1:12345")
    private UserBalanceService userBalanceServiceB;

    private final Long userId = 1L;

    @Override
    @HmilyTCC(confirmMethod = "tradeConfirm", cancelMethod = "tradeCancel")
    public boolean trade(BigDecimal money) throws Exception {

        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        if (dollarAccount.getMoney().intValue() >= money.intValue()) {
            update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", dollarAccount.getMoney().subtract(money)).set("frozen_money", dollarAccount.getFrozenMoney().add(money)));
            userBalanceServiceB.trade(money);
//            OkHttpClient client = new OkHttpClient();
//            final Request request = new Request.Builder()
//                    .url("http://localhost:8082/bank2/test/trade?money=" + money.intValue())
//                    .build();
//            String res = client.newCall(request).execute().body().string();
//            if (null == res || res.equalsIgnoreCase("false")) {
//                throw new Exception("trade fail");
//            }
            return true;
        } else {
            throw new Exception("no money");
        }
    }

    @Override
    public void tradeConfirm(BigDecimal money) {
        System.out.println("confirm trade bankA");
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("frozen_money", dollarAccount.getFrozenMoney().subtract(money)));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb").set("money", rmbAccount.getMoney().add(money.multiply(new BigDecimal(7)))));
    }

    @Override
    public void tradeCancel(BigDecimal money) {
        System.out.println("cancel trade bankA");
        UserBalanceEntity rmbAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "rmb"));
        UserBalanceEntity dollarAccount = getOne(new QueryWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar"));
        update(new UpdateWrapper<UserBalanceEntity>().eq("user_id", userId).eq("cash_type", "dollar").set("money", dollarAccount.getMoney().add(money)).set("frozen_money", dollarAccount.getFrozenMoney().subtract(money)));
    }
}
