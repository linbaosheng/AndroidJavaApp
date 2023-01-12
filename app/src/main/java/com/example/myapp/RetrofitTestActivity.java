package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapp.enity.Person;
import com.example.myapp.utils.LogUtils;
import com.example.myapp.net.data.BaseResponse;
import com.example.myapp.net.repository.PersonRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.operators.observable.ObservableSubscribeOn;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitTestActivity extends AppCompatActivity {

    public static void toRetrofitTestActivity(Context context) {
        Intent intent = new Intent(context, RetrofitTestActivity.class);
        context.startActivity(intent);
    }

    private Button btnInit;
    private Button btnTest;
    private Button btnObservable;
    private Button btnFlowable;
    private TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_test);

        btnInit = findViewById(R.id.btn_init);
        btnTest = findViewById(R.id.btn_test);
        btnObservable = findViewById(R.id.btn_observable);
        btnFlowable = findViewById(R.id.btn_flowable);
        tvShow = findViewById(R.id.tv_show);

        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnInit();
            }
        });

        btnTest.setOnClickListener(
                view -> {
                    btnTest();
                }
        );

        btnObservable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnObservable();
            }
        });

        btnFlowable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFlowable();
            }
        });
    }


    private void btnFlowable() {
        if (personRepository == null) {
            personRepository = new PersonRepository(); //减少对象的创建
        }
    }
    private Disposable disposable;
    //这里在页面结束后，需要置空
    //如果不是每条请求一个observer，那么在请求回掉的地方有可能发生线程锁的问题？
    private Observer<BaseResponse<Person>> observer = new Observer<BaseResponse<Person>>() {


        @Override
        public void onSubscribe(@NonNull Disposable d) {
            //每次请求产生的disposable是不一样的
            LogUtils.d("homeForObservable onSubscribe: " + d.hashCode());
            disposable = d;
        }

        @Override
        public void onNext(@NonNull BaseResponse<Person> personBaseResponse) {
            LogUtils.d("homeForObservable this.hashCode(): " + this.hashCode());
            LogUtils.d("homeForObservable onNext: " + personBaseResponse);
            LogUtils.d("homeForObservable tvShow.hashCode(): " + tvShow.hashCode());
            LogUtils.d("homeForObservable tvShow.getContext().hashCode(): " + tvShow.getContext().hashCode());
            tvShow.setText("Hello World!!!");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            LogUtils.d("homeForObservable onError: " + e);
        }

        @Override
        public void onComplete() {
            LogUtils.d("homeForObservable onComplete: ");
            //这里会默认调用dispose
//            disposable.dispose();
            //运行到这里，说明一个请求已经完成
            if (disposable != null) {
                disposable = null;
            }

        }
    };
    //需要置空，相当于在单例模式中，最后要置空
//    private static Observable<BaseResponse<Person>> homeForObservable;
    private Observable<BaseResponse<Person>> homeForObservable;
    private void btnObservable() {
        if (personRepository == null) {
            personRepository = new PersonRepository(); //减少对象的创建
        }
        //Observable 如果是静态对象
//        Observable<BaseResponse<Person>> homeForObservable = personRepository.homeForObservable();
        if (homeForObservable == null) {
//            homeForObservable = personRepository.homeForObservable();
            //这里可以只创建一次
            homeForObservable = personRepository.homeForObservable()
//                    .buffer(100)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        LogUtils.d("homeForObservable: " + homeForObservable.hashCode());
//        homeForObservable.doOnLifecycle(
//                new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Throwable {
//
//                    }
//                },
//                new Action() {
//                    @Override
//                    public void run() throws Throwable {
//
//                    }
//                });
        homeForObservable
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private PersonRepository personRepository;

    private void btnTest() {
        if (personRepository == null) {
            personRepository = new PersonRepository(); //减少对象的创建
        }
//        personRepository = new PersonRepository(); //如果每次都创建，会导致数据残留，内存升高
        //call一旦被使用，就无法再次使用，所以最好放在方法中创建，方法结束，jvm自动释放
        Call<BaseResponse<Person>> call = personRepository.home();
//        call.cancel();
        call.enqueue(new Callback<BaseResponse<Person>>() {
            @Override
            public void onResponse(Call<BaseResponse<Person>> call, Response<BaseResponse<Person>> response) {
                LogUtils.d("call.onResponse: " + call.hashCode());
                LogUtils.d("call.response.body(): " + response.body());
                LogUtils.d("call.response.message(): " + response.message());
                LogUtils.d("call.response call.isCanceled(): " + call.isCanceled());
                call.cancel();
            }

            @Override
            public void onFailure(Call<BaseResponse<Person>> call, Throwable t) {
                LogUtils.d("call.onFailure: " + call.hashCode());
                LogUtils.d("call.onFailure: " + t.getMessage());
            }
        });
        LogUtils.d("call.iscancle: " + call.isCanceled());
        LogUtils.d("call.isexecute: " + call.isExecuted());
    }

    private void btnInit() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("retrofit test activity onDestory");
        if (personRepository != null) {
            personRepository = null;
        }
        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose(); //如果这里没有调用会怎样，大概率会造成内存泄漏
            disposable = null;
        }
        if (observer != null){
            observer = null;
        }
    }
}