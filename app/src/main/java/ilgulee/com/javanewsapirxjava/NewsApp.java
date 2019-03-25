package ilgulee.com.javanewsapirxjava;

import android.app.Application;

import ilgulee.com.javanewsapirxjava.components.AppComponent;
import ilgulee.com.javanewsapirxjava.components.DaggerAppComponent;
import ilgulee.com.javanewsapirxjava.modules.AppModule;
import ilgulee.com.javanewsapirxjava.modules.RetrofitModule;
import timber.log.Timber;

public class NewsApp extends Application {
    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .retrofitModule(new RetrofitModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
