package com.example.myapp.net.engin1;

import com.example.myapp.net.ApiService;
import com.example.myapp.utils.LogUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//http://192.168.103.44:8911/hello/home
//viewmodel rxjava retrofit okhttp
//final 的左右禁止进行继承
public final class NetWorkEngin {
    private ApiService apiService;

    private void init() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.103.44:8911")
//                .client() //retrofit默认是okhttp，所以这里不需要设置，如果okhttp有特殊设置要求，需要设置进去，比如修改请求超时时间
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }

    }

    public ApiService getApiService() {
        if (apiService == null) {
            synchronized (NetWorkEngin.class) {
                if (apiService == null) {
                    init();
                }
            }
        }
        LogUtils.d("apiService: " + apiService.hashCode());
        return apiService;
    }

    private NetWorkEngin() {
        init();
    }

    private static NetWorkEngin netWorkEngin = new NetWorkEngin();

    public static NetWorkEngin getInstance() {
        if (netWorkEngin == null) {
            netWorkEngin = new NetWorkEngin();
        }
        netWorkEngin.init(); //每次进行判断
        LogUtils.d("netWorkEngin: " + netWorkEngin.hashCode());
        return netWorkEngin;
    }
}
