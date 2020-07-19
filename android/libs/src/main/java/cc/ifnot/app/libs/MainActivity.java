package cc.ifnot.app.libs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import cc.ifnot.app.libs.di.DaggerMainComponent;
import cc.ifnot.libs.utils.Lg;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    static {
        Lg.tag(BuildConfig.name);
        Lg.level(Lg.MORE);
    }

    @Inject
    OkHttpClient mOkHttpClient;
    @Inject
    IService service;
    @Inject
    ExecutorService pool;
    @Inject
    CompositeDisposable cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerMainComponent.create().inject(this);
//        DaggerMainComponent.builder().build().inject(this);

        cd.add(service.get().observeOn(Schedulers.from(pool))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Throwable {
                        Lg.w("==> %s", s);
                    }
                }));


        Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Lg.w("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Lg.w("onNext");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Lg.w("onError");
                    }

                    @Override
                    public void onComplete() {
                        Lg.w("onComplete");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cd.isDisposed()) {
            cd.clear();
        }
    }
}
