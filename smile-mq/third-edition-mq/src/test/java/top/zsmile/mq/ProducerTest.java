package top.zsmile.mq;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ProducerTest {


    private final MediaType JSONTYPE = MediaType.parse("application/json;charset=utf-8");

    @Test
    public void writeDate() throws IOException, InterruptedException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String topic = "test";

        for (int i = 1; i <= 10; i++) {
            SmqMessage message = new SmqMessage(null, "this is " + i);
            RequestBody requestBody = RequestBody.create(JSONTYPE, JSON.toJSONString(message));
            Request request = new Request.Builder().url("http://localhost:8080/smq/send?topic=" + topic).post(requestBody).build();
            Call call = okHttpClient.newCall(request);
            Response execute = call.execute();
            ResponseBody body = execute.body();
            System.out.println("producer=>" + body.string());
            Thread.sleep(100);
        }
    }
}
