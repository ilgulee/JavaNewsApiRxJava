package ilgulee.com.javanewsapirxjava.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ilgulee.com.javanewsapirxjava.NewsApp;
import ilgulee.com.javanewsapirxjava.R;
import ilgulee.com.javanewsapirxjava.data.network.CurrentHeadlineResponse;
import ilgulee.com.javanewsapirxjava.data.network.NewsApiService;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    @Inject
    Retrofit mRetrofit;
    private NewsApiService newsApiService;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NewsApp) getActivity().getApplicationContext()).getAppComponent().inject(this);
        newsApiService = mRetrofit.create(NewsApiService.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsApiService.getHeadlines("us")
                .subscribeOn(Schedulers.io())
                .map(CurrentHeadlineResponse::getArticles)
                .flatMap(Flowable::fromIterable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(article -> {
                    // mNoData.setVisibility(View.GONE);
                    Log.d(TAG, "onActivityCreated: " + article.toString());
                }, error -> {
                    //mNoData.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onActivityCreated: " + error.getMessage());
                    // getDatabase();
                }, () -> {
                    Log.d(TAG, "onActivityCreated: completed");
                });
    }

}
