package top.zsmile.kafka.demo;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private Long id;
    private String content;
    private Date sendTime;
}
