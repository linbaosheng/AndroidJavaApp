package com.example.myapp.net.repository;

import com.example.myapp.enity.Person;
import com.example.myapp.net.data.BaseResponse;
import com.example.myapp.net.engin1.NetWorkEngin;
import com.example.myapp.net.engin2.NetWorkEngin2;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
//仓库模式，viewmodel中间件作用，数据在model层向activity层发送
public class PersonRepository {
    //外部传入进来？
//    private ApiService apiService;
    //基于call返回，然后在viewmodel中处理，结合lifecycle和livedata使用，基本满足大部分网络的使用，需要注意对象的创建和销毁
    public Call<BaseResponse<Person>> home(){
        return NetWorkEngin.getInstance().getApiService().home();
    }

    public Observable<BaseResponse<Person>> homeForObservable(){
        return NetWorkEngin2.getInstance().getApiService().homeForObservable();
    }

    public Flowable<BaseResponse<Person>> homeForFlowable(){
        return NetWorkEngin2.getInstance().getApiService().homeForFlowable();
    }
}
