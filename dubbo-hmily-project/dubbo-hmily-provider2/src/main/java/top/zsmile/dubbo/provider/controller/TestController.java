package top.zsmile.dubbo.provider.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.zsmile.dubbo.provider.service.UserBalanceService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/test")
public class TestController {

    @DubboReference(version = "2.0.0", tag = "bankB") //, url = "dubbo://127.0.0.1:12345")
    private UserBalanceService userBalanceServiceB;

    @RequestMapping("/trade")
    public boolean trade(@RequestParam("money") BigDecimal money) throws Exception {
        return this.userBalanceServiceB.trade(money);
    }
}
