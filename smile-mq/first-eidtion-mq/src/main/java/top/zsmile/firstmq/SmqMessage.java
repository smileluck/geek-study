package top.zsmile.firstmq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
public class SmqMessage<T> {
    private HashMap<String, Object> header;
    private T body;
}
