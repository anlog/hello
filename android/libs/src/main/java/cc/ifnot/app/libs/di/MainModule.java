package cc.ifnot.app.libs.di;

import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Singleton;

import cc.ifnot.app.libs.BuildConfig;
import cc.ifnot.app.libs.IService;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * author: dp
 * created on: 2020/7/19 4:54 PM
 * description:
 */
@Module
public class MainModule {

    @Singleton
    @Provides
    HttpLoggingInterceptor providerHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Singleton
    @Provides
    OkHttpClient okHttpClient(HttpLoggingInterceptor mHttpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(mHttpLoggingInterceptor)
                .build();
    }

    @Singleton
    @Provides
    Retrofit providerRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    IService providerIService(Retrofit retrofit) {
        return retrofit.create(IService.class);
    }

    @Singleton
    @Provides
    ThreadFactory providerThreadFactory() {
        return new ThreadFactory() {
            AtomicLong id = new AtomicLong();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, BuildConfig.name + "_" + Process.myPid() + "_" + id.getAndIncrement());
            }
        };
    }

    @Singleton
    @Provides
    ExecutorService providerExecutorService(ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                threadFactory);
    }

    @Singleton
    @Provides
    CompositeDisposable providerCompositeDisposable() {
        return new CompositeDisposable();
    }

}
