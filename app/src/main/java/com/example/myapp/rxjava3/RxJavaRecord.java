package com.example.myapp.rxjava3;

import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 *
 * Observable:最基本的响应类型，不支持背压
 * Flowable:最基本的响应类型，支持背压
 *
 * Single只发射一个元素，发射onSuccess或onError方法，所以没有complete方法，不像Observable或者Flowable，数据发射完成之后，需要调用complete告诉下游已经完成。
 *
 * Completable  不会发射数据，只会给下游发送一个信号。回调onComplete或onError方法。
 *
 * Maybe是Single和Completable的结合，需要注意的是onSuccess和onComplete方法只会执行其中一个，这不同于Observable和Flowable最后是以onComplete()结尾。
 */
public class RxJavaRecord {

//    Observable 被观察者 生产
    //Observer 观察者 消费
    public void test(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {

            }
        })
                .subscribe(//注册观察者对象
                        new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    //背压 flow 默认是128条数据，通过 request设置最大处理数量
    private final Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
        @Override
        public void subscribe(FlowableEmitter<String> e) throws Exception {
            e.onNext("1");
            e.onNext("2");
            e.onNext("3");
            e.onNext("4");
            e.onComplete();
        }
    }, BackpressureStrategy.MISSING);
    public void test2(){
//        Flowable.just(1,2,34,5).onBackpressureBuffer(2).subscribe();
        flowable.subscribe(new FlowableSubscriber<String>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(100); //处理最大数量
            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
