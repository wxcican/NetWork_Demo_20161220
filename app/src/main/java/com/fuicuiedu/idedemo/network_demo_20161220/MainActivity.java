package com.fuicuiedu.idedemo.network_demo_20161220;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.R.attr.password;
import static android.R.string.ok;

public class MainActivity extends AppCompatActivity {

    EditText mUserName,mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserName = (EditText) findViewById(R.id.main_username);
        mPassword = (EditText) findViewById(R.id.main_password);

        findViewById(R.id.main_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserName.getText().toString();
                String password = mPassword.getText().toString();

                register(username,password);
            }
        });
    }

    private void register(String username,String password){

        //实例化日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //设置日志拦截器级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //自定义拦截器，用于同一添加bomb必要的头信息
        BombInterceptor bombInterceptor = new BombInterceptor();

        //拿到客户端
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //加入同一添加头信息的拦截器
                .addInterceptor(bombInterceptor)
                //加入日志拦截器
                .addInterceptor(httpLoggingInterceptor)
                .build();

        //构建请求
        //因为是post请求，所以构建请求体

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username",username);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(null,jsonObject.toString());

        Request request = new Request.Builder()
                //请求方式（请求体）
                .post(requestBody)
                .url("https://api.bmob.cn/1/users")
                .build();

        //客户端执行请求->拿到响应
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("aaa","超时，无网络连接");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //是否连接成功
                if (response.isSuccessful()){
                    Log.e("aaa","拿到响应");
                }else{
                    Log.e("aaa","响应失败，响应码=" + response.code());
                }
            }
        });


    }

}
