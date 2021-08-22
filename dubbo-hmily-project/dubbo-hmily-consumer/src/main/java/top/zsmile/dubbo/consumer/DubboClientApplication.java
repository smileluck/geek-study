package top.zsmile.dubbo.consumer;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import top.zsmile.dubbo.provider.service.UserBalanceService;
import top.zsmile.dubbo.provider.service.UserService;

import java.math.BigDecimal;

@SpringBootApplication
public class DubboClientApplication {

    @DubboReference(version = "1.0.0", tag = "bankA") //, url = "dubbo://127.0.0.1:12345")
    private UserService userServiceA;

    @DubboReference(version = "1.0.0", tag = "bankA") //, url = "dubbo://127.0.0.1:12345")
    private UserBalanceService userBalanceServiceA;

    public static void main(String[] args) {

        SpringApplication.run(DubboClientApplication.class).close();

        // UserService service = new xxx();
        // service.findById

//		UserService userService = Rpcfx.create(UserService.class, "http://localhost:8080/");
//		User user = userService.findById(1);
//		System.out.println("find user id=1 from server: " + user.getName());
//
//		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8080/");
//		Order order = orderService.findOrderById(1992129);
//		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));

    }

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            userBalanceServiceA.trade(new BigDecimal(1));
        };
    }

}
