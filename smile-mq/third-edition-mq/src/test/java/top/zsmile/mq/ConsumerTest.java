package top.zsmile.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ConsumerTest {
    @Test
    public void readData() throws IOException, InterruptedException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String topic = "test";
        Request request = new Request.Builder().url("http://localhost:8080/smq/offset?topic=" + topic).get().build();
        while (true) {
            Call call = okHttpClient.newCall(request);
            Response execute = call.execute();
            ResponseBody body = execute.body();
//
            JSONObject resObj = JSON.parseObject(body.string());
            if (resObj.getString("data") != null) {
                System.out.println("consumer=>" + resObj.getString("data"));
            }
            Thread.sleep(100);
        }
    }
}
