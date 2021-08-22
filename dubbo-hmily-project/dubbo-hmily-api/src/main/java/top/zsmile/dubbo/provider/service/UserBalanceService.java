package top.zsmile.dubbo.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.zsmile.dubbo.entity.UserBalanceEntity;

import java.math.BigDecimal;

/**
 * @author zsmile
 * @email zsmile@gmail.com
 * @date 2021-08-22 22:07:54
 */
public interface UserBalanceService extends IService<UserBalanceEntity> {
    public void trade(BigDecimal money) throws Exception;

    public void tradeConfirm(BigDecimal money);

    public void tradeCancel(BigDecimal money);
}

