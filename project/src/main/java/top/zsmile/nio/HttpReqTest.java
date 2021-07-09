package top.zsmile.nio;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;


public class HttpReqTest {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();


        try {
            Request request = new Request.Builder().url("http://localhost:8801").get().build();
            Response execute = client.newCall(request).execute();
            System.out.println("响应内容为：" + execute.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
