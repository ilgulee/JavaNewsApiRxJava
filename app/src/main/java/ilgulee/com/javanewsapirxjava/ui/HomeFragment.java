package ilgulee.com.javanewsapirxjava.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ilgulee.com.javanewsapirxjava.NewsApp;
import ilgulee.com.javanewsapirxjava.R;
import ilgulee.com.javanewsapirxjava.data.db.ArticleDao;
import ilgulee.com.javanewsapirxjava.data.db.ArticleRoomDatabase;
import ilgulee.com.javanewsapirxjava.data.db.entity.Article;
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
    private RecyclerView mRecyclerView;
    private List<Article> mArticles;
    private TextView mNoData;
    private HeadlineAdapter mAdapter;
    private ArticleRoomDatabase db;
    private ArticleDao mArticleDao;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticles = new ArrayList<>();
        ((NewsApp) getActivity().getApplicationContext()).getAppComponent().inject(this);
        newsApiService = mRetrofit.create(NewsApiService.class);
        db = ArticleRoomDatabase.getINSTANCE(getActivity().getApplication());
        mArticleDao = db.mArticleDao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new HeadlineAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mNoData = view.findViewById(R.id.textview_no_data);
        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsApiService.getHeadlines("us")
//                .flatMap(i-> Flowable.<CurrentHeadlineResponse>error(new RuntimeException("test crash for fallback")))
                .doOnNext(i -> {
                    mArticleDao.deleteAll();
                    Log.d(TAG, "onActivityCreated: DB cleared");
                })
                .subscribeOn(Schedulers.io())
                .map(CurrentHeadlineResponse::getArticles)
                .flatMap(Flowable::fromIterable)
                .doOnNext(article -> {
                    mArticles.add(article);
                    mArticleDao.insertHeadline(article);
                })
                .onExceptionResumeNext(i -> getDatabase())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(article -> {
                    mNoData.setVisibility(View.GONE);
                    Log.d(TAG, "onActivityCreated: " + article.toString());
                }, error -> {
                    mNoData.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onActivityCreated: " + error.getMessage());
                    // getDatabase();
                }, () -> {
                    Log.d(TAG, "onActivityCreated: completed");
                    mNoData.setVisibility(View.GONE);
                    mAdapter.dataUpdate(mArticles);
                });
    }

    @SuppressLint("CheckResult")
    private void getDatabase() {
        mArticleDao.getHeadlines()
                .subscribeOn(Schedulers.io())
                .flatMap(Flowable::fromIterable)
                .doOnNext(article -> mArticles.add(article))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(article -> {
                    Log.d(TAG, "getDatabase: " + article.toString());
                    mArticles.add(article);
                    if (mArticles.size() == 0) {
                        Log.d(TAG, "getDatabase: completed");
                        mNoData.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG, "getDatabase: completed");
                        mNoData.setVisibility(View.GONE);
                        mAdapter.dataUpdate(mArticles);
                    }
                }, error -> {

                }, () -> {

                });


    }

    private class HeadlineAdapter extends RecyclerView.Adapter<HomeFragment.ViewHolder> {
        List<Article> mArticles;
        Context mContext;

        public HeadlineAdapter(Context context) {
            mArticles = new ArrayList<>();
            mContext = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Article article = mArticles.get(position);
            holder.bind(mContext, article);
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }

        public void dataUpdate(List<Article> articles) {
            mArticles.clear();
            mArticles.addAll(articles);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView source;
        TextView update;
        TextView title;
        TextView description;
        ImageView photo;
        Article mArticle;
        Context mContext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.textview_source_name);
            update = itemView.findViewById(R.id.textview_update);
            title = itemView.findViewById(R.id.textview_title);
            description = itemView.findViewById(R.id.textview_description);
            photo = itemView.findViewById(R.id.imageview_photo);
        }

        @Override
        public void onClick(View v) {

        }

        public void bind(Context context, Article article) {
            this.mContext = context;
            this.mArticle = article;
            source.setText(mArticle.getSource().getName());
            update.setText(mArticle.getPublishedAt());
            title.setText(mArticle.getTitle());
            description.setText(mArticle.getDescription());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_cloud_download);
            Glide.with(mContext)
                    .asBitmap()
                    .load(mArticle.getUrlToImage())
                    .apply(requestOptions)
                    .into(photo);
        }
    }
}
