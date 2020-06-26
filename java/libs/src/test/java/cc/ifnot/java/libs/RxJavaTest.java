package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import cc.ifnot.libs.utils.Lg;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: dp
 * created on: 2020/6/21 5:06 PM
 * description:
 */
class RxJavaTest {

    @BeforeAll
    static void setUpAll() {
        Lg.tag("RxJavaTest");
        Lg.level(Lg.MORE);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testRxFlowable() {
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v -> v * v)
                .sequential()
                .blockingSubscribe(Lg::d);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.ipify.org?format=json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            Lg.d("ok: %s", Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testRxBasic() {
        @NonNull Disposable disposable = Flowable.fromCallable(() -> {
            Thread.sleep(1000);
            Lg.d("fromCallable: Done");
            return "Done!";
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(s -> Lg.d("subscribe: %s", s));

        while (!disposable.isDisposed()) {
            Lg.d("yield");
            Thread.yield();
        }
    }

    @Test
    @DisplayName("java")
    public void test() throws ExecutionException, InterruptedException {
        Flowable.just("hello world!").subscribe(Lg::d);

        Flowable.range(0, 5).concatMap((Function<Integer, Publisher<Integer>>) integer -> {
            Lg.d("concatMap: %s", integer);
            return Flowable.just(integer).subscribeOn(Schedulers.computation());
        }).blockingSubscribe(integer -> Lg.d("blockingSubscribe: %s", integer));
        Lg.d("======================");

        Flowable.range(1, 5).flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public Publisher<Integer> apply(Integer integer) throws Throwable {
                Lg.d("flatMap: %s", integer);
                return Flowable.just(integer)
                        .subscribeOn(Schedulers.io())
                        .map(integer1 -> {
                                    Lg.d("map: %s", integer1);
                                    return integer1 * integer1;
                                }
                        );
            }
        }).blockingSubscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer o) throws Throwable {
                Lg.d("blockingSubscribe: %s", o);
            }
        });

        Flowable.range(1, 10).map(integer -> {
            Lg.d("map %s", integer);
            return integer * integer;
        }).filter(integer -> {
            Lg.d("filter %s", integer);
            return integer % 3 == 0;
        }).subscribe(integer -> Lg.d("subscribe %s", integer));

        Observable.create((ObservableOnSubscribe<Long>) emitter -> {
            while (!emitter.isDisposed()) {
                long timeMillis = System.currentTimeMillis();
                emitter.onNext(timeMillis);
                if (timeMillis % 2 != 0) {
                    emitter.onError(new IllegalStateException("old timeMillis"));
                    break;
                }
            }
        }).subscribe(aLong -> Lg.d("subscribe: %d", aLong), Throwable::printStackTrace);
    }

    @AfterEach
    void tearDown() {
    }
}