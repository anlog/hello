package cc.ifnot.app.libs;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

/**
 * author: dp
 * created on: 2020/7/19 5:36 PM
 * description:
 */
public interface IService {

    @GET("/")
    Observable<String> get();
}
