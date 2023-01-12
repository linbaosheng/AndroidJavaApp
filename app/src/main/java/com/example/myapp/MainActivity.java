package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapp.enity.Person;
import com.example.myapp.rxjava3.RxUtils;
import com.example.myapp.utils.LogUtils;

import org.reactivestreams.Publisher;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableConverter;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ObserverableTAG";

    private Button btn1;
    private Button btn2;
    private Button btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn3.setOnClickListener(view -> {
//            disposable.dispose();
            RetrofitTestActivity.toRetrofitTestActivity(MainActivity.this);
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                RxUtils.click(btn1, 2, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Log.i(TAG, "btn onclick");
//                    }
//                });
                emitter1.onNext("1222");
                emitter1.onNext("1dsfasf");
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                test1();
//                test2();
//                test3();
//                test4();
//                test5();
//                test6();
//                test7();
//                verify(view);
//                test8();
//                test9();
                for (int i = 0; i < 200; i++) {
                }
                    test10();
            }
        });
    }

    private void test10() {
        Flowable.create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Throwable {
                        String uuid = UUID.randomUUID().toString();

                        for (int i = 0; i < 200; i++) {
                            emitter.onNext("uuid - " + i);
                            LogUtils.d("subscribe: " + "uuid - " + i);
                        }
                    }
                }, BackpressureStrategy.BUFFER)
                .compose(new FlowableTransformer<String, String>()  {
                    @Override
                    public @NonNull Publisher<String> apply(@NonNull Flowable<String> upstream) {
                        LogUtils.d("compose Publisher apply");
                        return upstream.map(new Function<String, String>() {
                            @Override
                            public String apply(String s) throws Throwable {
                                LogUtils.d("compose apply: " + s);
//                                Thread.sleep(100);
                                return s;
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Throwable {
                                Thread.sleep(2000);
                                LogUtils.d("accept: " + s);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Throwable {
                                LogUtils.d("accept Throwable: " + throwable);
                            }
                        }
                );
    }

    private void test9() {
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                        for (int i = 0; i < 200; i++) {
                            LogUtils.d("onnext: " + i);
                            emitter.onNext("i = " + i);//线程切换，这里会往缓存池中存放，如果处理太慢，并且又退出当前处理，缓存池会一直存在该数据
                        }
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtils.d("onSubscribe: " + d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        LogUtils.d("onNext1: " + s);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtils.d("onNext2: " + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.d("onError: ");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("onComplete: ");
                    }
                });
    }

    private ObservableEmitter<String> emitter1;
    private Disposable disposable;

    private void test8() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                emitter1 = emitter;
            }
        });
//        String x = observable.to(new ObservableConverter<String, String>()  {
//            @Override
//            public String apply(@NonNull Observable<String> upstream) {
//                return "123123";
//            }
//        });
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
                Log.i(TAG, "onSubscribe d = " + d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.i(TAG, "onnext s -> " + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        });

    }

    public void verify(View view) {
        final long count = 10;//倒计时时间
        final Button button = (Button) view;//当前按钮

        Observable.intervalRange(0, count, 0, 1, TimeUnit.SECONDS)//定时器
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        Log.i(TAG, "aLong:" + aLong + ", thread:" + Thread.currentThread().getName());
                        return count - aLong;//将值转换下，当前值：3，2，1，0
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//主线程更新UI
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        //监听订阅时，将按钮设置为不可点击
                        button.setEnabled(false);
                        button.setTextColor(Color.BLACK);
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe:" + d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        //设置倒计时文本
                        button.setText("剩余" + aLong + "秒");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        //事件完成后恢复点击
                        button.setEnabled(true);
                        button.setText("发送验证码");
                    }
                });
    }


    private void test7() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                Log.i(TAG, "subscrbe emitter");
            }
        }).subscribe();
    }

    private void test6() {
        Observable.interval(1, TimeUnit.SECONDS)
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Throwable {
                        Log.i(TAG, "apply " + aLong + " thread:" + Thread.currentThread().getName());
                        return "xxx";
                    }
                })
                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String aLong) throws Throwable {
                        Log.i(TAG, "accetp " + aLong + " thread:" + Thread.currentThread().getName());
                    }
                });
    }

    private void test5() {
        Disposable dis = Observable.fromArray("1", 1, 2, new Person())
                .subscribe(new Consumer<Serializable>() {
                               @Override
                               public void accept(Serializable serializable) throws Throwable {
                                   Log.i(TAG, "accept seri " + serializable.toString());
                                   if (serializable instanceof Person) {
                                       Person person = (Person) serializable;
                                       Log.i(TAG, "accept seri is person");
                                   }
                               }
                           }

                );

    }

    private static CompositeDisposable mRxEvent = new CompositeDisposable();

    private void test4() {
        Disposable dis = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                        emitter.onNext("1");
                        emitter.onNext("2");
                        emitter.onNext("3");
                        Thread.sleep(2000);
//                emitter.onError(new Throwable("error throw"));
                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
//                throw new NullPointerException("is null");
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Consumer<String>() { //onnext
                            @Override
                            public void accept(String s) throws Throwable {
                                Log.i(TAG, "Consumer accept s " + s);
                            }
                        },
                        new Consumer<Throwable>() { //onthrowable
                            @Override
                            public void accept(Throwable throwable) throws Throwable {
                                Log.i(TAG, "Consumer accept  throwable " + throwable.getMessage());
                            }
                        },
                        new Action() { //action
                            @Override
                            public void run() throws Throwable {
                                Log.i(TAG, "Action run  ");
                            }
                        }
                );
        mRxEvent.add(dis);
        btn1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "rxevent remove dis " + dis);
                mRxEvent.remove(dis);
            }
        }, 1000);
    }

    private void test3() {
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                        emitter.onNext("1");
                        emitter.onNext("2");
                        emitter.onNext("3");
                        emitter.onNext("4");
                        emitter.onComplete();
                        Log.i(TAG, "subscribe thread is " + Thread.currentThread().getName());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i(TAG, "onSubscribe is " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.i(TAG, "onNext is " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i(TAG, "onError is " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete is " + Thread.currentThread().getName());
                    }
                });
    }

    private void test2() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
//                emitter.onError(new Throwable("error throw"));
//                emitter.onComplete();
                throw new NullPointerException("is null");
            }
        }).subscribe(
                new Consumer<String>() { //onnext
                    @Override
                    public void accept(String s) throws Throwable {
                        Log.i(TAG, "Consumer accept s " + s);
                    }
                },
                new Consumer<Throwable>() { //onthrowable
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.i(TAG, "Consumer accept  throwable " + throwable.getMessage());
                    }
                },
                new Action() { //action
                    @Override
                    public void run() throws Throwable {
                        Log.i(TAG, "Action run  ");
                    }
                }
        );
    }

    private void test1() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
//                emitter.onError(new Throwable("error throw"));
                emitter.onComplete();
            }
        });

        Observer<String> observer = new Observer<String>() {
            private Disposable d;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i(TAG, "onSubscribe  " + d);
                this.d = d;
            }

            @Override
            public void onNext(String o) {
                Log.i(TAG, "onnext " + o);
                this.d.dispose();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete ");
            }
        };

        observable.subscribe(observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}