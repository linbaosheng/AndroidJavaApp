package com.example.myapp.net.engin2;

import com.example.myapp.net.ApiService;
import com.example.myapp.utils.LogUtils;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//http://192.168.103.44:8911/hello/home
//viewmodel rxjava retrofit okhttp
//final 的左右禁止进行继承
public final class NetWorkEngin2 {
    private ApiService apiService;

    private void init() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.103.44:8911")
//                .client() //retrofit默认是okhttp，所以这里不需要设置，如果okhttp有特殊设置要求，需要设置进去，比如修改请求超时时间
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) //如果需要返回rxjava
                    .build();
            apiService = retrofit.create(ApiService.class);
        }

    }

    public ApiService getApiService() {
        if (apiService == null) {
            synchronized (NetWorkEngin2.class) {
                if (apiService == null) {
                    init();
                }
            }
        }
        return apiService;
    }

    private NetWorkEngin2() {
        init();
    }

    private static NetWorkEngin2 netWorkEngin = new NetWorkEngin2();

    public static NetWorkEngin2 getInstance() {
        if (netWorkEngin == null) {
            netWorkEngin = new NetWorkEngin2();
        }
        netWorkEngin.init(); //每次进行判断
        LogUtils.d("netWorkEngin: " + netWorkEngin.hashCode());
        return netWorkEngin;
    }
}
