package ilgulee.com.javanewsapirxjava.data.network;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v2/top-headlines")
    Flowable<CurrentHeadlineResponse> getHeadlines(@Query("country") String country);
}
