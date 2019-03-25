package ilgulee.com.javanewsapirxjava.modules;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ilgulee.com.javanewsapirxjava.BuildConfig;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = AppModule.class)
public class OkHttpClientModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    public Cache provideCache(File cacheFile) {
        return new Cache(cacheFile, 10 * 1024 * 1024);
    }

    @Provides
    @Singleton
    public File provideFile(Application application) {
        File file = new File(application.getCacheDir(), "HttpCache");
        file.mkdirs();
        return file;
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            Timber.d(message);
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(Cache cache,
                                            HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient()
                .newBuilder()
                .cache(cache)
                .addInterceptor(chain -> {
                    final Request originalRequest = chain.request();
                    final HttpUrl originalUrl = originalRequest.url();

                    final HttpUrl url = originalUrl.newBuilder()
                            .addQueryParameter("apiKey", BuildConfig.API_KEY)
                            .build();
                    final Request.Builder requestBuilder = originalRequest.newBuilder().url(url);
                    final Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

}
