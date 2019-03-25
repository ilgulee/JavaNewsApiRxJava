package ilgulee.com.javanewsapirxjava.components;

import javax.inject.Singleton;

import dagger.Component;
import ilgulee.com.javanewsapirxjava.modules.AppModule;
import ilgulee.com.javanewsapirxjava.modules.RetrofitModule;
import ilgulee.com.javanewsapirxjava.ui.HomeFragment;

@Singleton
@Component(modules = {AppModule.class, RetrofitModule.class})
public interface AppComponent {
    void inject(HomeFragment homeFragment);
}