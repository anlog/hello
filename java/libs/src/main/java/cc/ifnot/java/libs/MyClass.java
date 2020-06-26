package cc.ifnot.java.libs;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import cc.ifnot.libs.utils.Lg;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyClass {

    static {
        Lg.tag("MyClass");
        Lg.level(Lg.MORE);
    }

    public static void main(String[] args) throws InterruptedException {

//        CompletableFuture.runAsync()
        CountDownLatch latch = new CountDownLatch(1);
        Completable.fromCallable((Callable<Void>) () -> {
            Lg.d("call");
            return null;
        }).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Lg.d("onSubscribe");
            }

            @Override
            public void onComplete() {
                Lg.d("onComplete");
                latch.countDown();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Lg.d("onError");
            }
        });
        latch.await();
    }
}