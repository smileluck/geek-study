package top.zsmile.redis.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class OrderEntity implements Serializable {
    private int id;
    private int money;
}
