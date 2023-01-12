package com.example.myapp.net;

import com.example.myapp.enity.Person;
import com.example.myapp.net.data.BaseResponse;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("hello/home")
    Call<BaseResponse<Person>> home();

    //盲猜这里返回的对象是动态对象，所以每次返回的hash值不一样
    @GET("hello/home")
    Observable<BaseResponse<Person>> homeForObservable();

    @GET("hello/home")
    Flowable<BaseResponse<Person>> homeForFlowable();


}
