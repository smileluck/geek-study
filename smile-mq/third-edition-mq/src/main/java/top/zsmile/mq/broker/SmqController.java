package top.zsmile.mq.broker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zsmile.mq.SmqMessage;

@RestController
@RequestMapping("/smq")
public class SmqController {

    @Autowired
    private SmqChannelManage smqChannelManage;

    @PostMapping("/send")
    public R send(@RequestParam("topic") String topic, @RequestBody SmqMessage message) {
        SmqChannel smqChannel = smqChannelManage.connectTopic(topic);
        boolean send = smqChannel.send(message);
        if (send) {
            return R.ok("发送成功");
        } else {
            return R.error("发送失败");
        }
    }

    @GetMapping("/offset")
    public R poll(@RequestParam("topic") String topic) {
        SmqChannel smqChannel = smqChannelManage.connectTopic(topic);
        SmqMessage message = smqChannel.offset();
        return R.ok().put("data", message);
    }


    @GetMapping("/get")
    public R poll(@RequestParam("topic") String topic, @RequestParam("readIndex") int readIndex) {
        SmqChannel smqChannel = smqChannelManage.connectTopic(topic);
        SmqMessage message = smqChannel.get(readIndex);
        return R.ok().put("data", message);
    }
}
