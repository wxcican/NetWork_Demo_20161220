package com.fuicuiedu.idedemo.network_demo_20161220;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fuicuiedu.idedemo.network_demo_20161220.entity.RegisterResult;
import com.fuicuiedu.idedemo.network_demo_20161220.entity.User;
import com.google.gson.Gson;

import org.json.JSONArray;
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
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.R.attr.password;
import static android.R.string.ok;

public class MainActivity extends AppCompatActivity {

    EditText mUserName,mPassword;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, "更新UI", Toast.LENGTH_SHORT).show();
        }
    };

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

//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("username",username);
//            jsonObject.put("password",password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        User user = new User(username, password);
        //通过Gson将一个user类生成为一个json数据
        String userJson = new Gson().toJson(user);
        Log.e("userJson",userJson);

        final RequestBody requestBody = RequestBody.create(null,userJson);

        Request request = new Request.Builder()
                //请求方式（请求体）
                .post(requestBody)
                .url("https://api.bmob.cn/1/users")
                .build();

        //客户端执行请求->拿到响应
        Call call = okHttpClient.newCall(request);
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureInUI(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "超时或者无网络连接。", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponseInUI(Call call, String body) {
                Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                RegisterResult result = new Gson().fromJson(body,RegisterResult.class);
                Log.e("RegisterResult",result.toString());
            }
        });











//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("aaa","超时，无网络连接");
//            }
//
//            //网络连接成功
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                //模拟更新UI
//                handler.sendEmptyMessage(0);
//                //是否成功
//                if (response.isSuccessful()){
//                    //拿到响应体
//                    ResponseBody responseBody = response.body();
//                    //响应体是json格式
//                    String json = responseBody.string();
//                    //通过Gson讲json数据解析成一个实体类
//                    RegisterResult registerResult = new Gson().fromJson(json,RegisterResult.class);
////                    {
////                        "createdAt": "2016-12-21 10:36:04",
////                            "objectId": "8ba8abb9cd",
////                            "sessionToken": "821bea7740c814b88090ed8ae5a417e9"
////                    }
////                    try {
////                        JSONObject jsonObject = new JSONObject(json);
////                        registerResult.setCreatedAt(jsonObject.getString("createdAt"));
////                        registerResult.setObjectId(jsonObject.getString("objectId"));
////                        registerResult.setSessionToken(jsonObject.getString("sessionToken"));
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
//
//                    Log.e("RegisterResult",registerResult.toString());
//
//                }else{
//                    Log.e("aaa","响应失败，响应码=" + response.code());
//                }
//            }
//        });


    }

}
