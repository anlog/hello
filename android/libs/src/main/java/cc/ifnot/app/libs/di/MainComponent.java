package cc.ifnot.app.libs.di;

import javax.inject.Singleton;

import cc.ifnot.app.libs.MainActivity;
import dagger.Component;

/**
 * author: dp
 * created on: 2020/7/19 5:08 PM
 * description:
 */
@Singleton
@Component(modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity activity);
}
