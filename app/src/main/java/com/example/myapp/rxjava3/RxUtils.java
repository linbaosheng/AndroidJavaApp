package com.example.myapp.rxjava3;

import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;

public class RxUtils {
    private static final String TAG = "ObserverableTAG";
    public static void click(final View view, long seconds, View.OnClickListener clickListener) {
//        Observable.create()
        new ViewClickObservable(view)
                .throttleFirst(seconds, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "btn onclick 3");
                        if (view != null) {
                            Log.i(TAG, "btn onclick 4");
                            view.setOnClickListener(null);
                        }
                    }
                }).subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.i(TAG, "btn onclick 2");
                        clickListener.onClick(view);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //创建一个观察者
    private static class ViewClickObservable extends Observable<Object> {

        private View view;

        public ViewClickObservable(View view) {
            this.view = view;
        }

        //当这个观察者被订阅的时候，会执行下面的回调
        @Override
        protected void subscribeActual(final Observer<? super Object> observer) {
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "btn onclick 1");
                        observer.onNext(v);
                    }
                });
            }
        }
    }
}

