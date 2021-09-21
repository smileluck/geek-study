package top.zsmile.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@AllArgsConstructor
@Data
public class SmqMessage<T> {
    private HashMap<String, Object> attrs;
    private T body;
}
