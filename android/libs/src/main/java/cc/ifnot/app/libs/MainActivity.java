package cc.ifnot.app.libs;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
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
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ax);
        if (true) {
            return;
        }

//        final RecyclerView rv = findViewById(R.id.rv);
//
//        rv.setLayoutManager(new GridLayoutManager(this, 2));
//        adapter = new EPAdapter();
//
//        rv.setAdapter(adapter);


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

    private static class EPAdapter extends RecyclerView.Adapter<EPAdapter.ViewHolder> {

        private List<MediaInfo> data;

        @androidx.annotation.NonNull
        @Override
        public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
            final ImageView iv = new ImageView(parent.getContext());
            iv.setPadding(10, 10, 10, 10);
            return new ViewHolder(iv);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
//            GlideApp.with(holder.iv).load(data.get(0))
        }

        @Override
        public int getItemCount() {
            return data != null ? data.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;

            public ViewHolder(@androidx.annotation.NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
