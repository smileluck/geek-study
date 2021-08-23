package top.zsmile.dubbo.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.hmily.annotation.Hmily;
import top.zsmile.dubbo.entity.UserBalanceEntity;

import java.math.BigDecimal;

/**
 * @author zsmile
 * @email zsmile@gmail.com
 * @date 2021-08-22 22:07:54
 */
public interface UserBalanceService extends IService<UserBalanceEntity> {

    @Hmily
    public boolean trade(BigDecimal money) throws Exception;

    public void tradeConfirm(BigDecimal money);

    public void tradeCancel(BigDecimal money);
}

