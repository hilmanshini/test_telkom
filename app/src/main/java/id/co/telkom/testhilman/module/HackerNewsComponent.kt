package id.co.telkom.testhilman.module

import dagger.Component
import id.co.telkom.testhilman.FeedActivity
import id.co.telkom.testhilman.MainActivity

@Component(modules = arrayOf(HackerNewsModule::class))
abstract class HackerNewsComponent{

    public abstract fun inject(activity: MainActivity);
    public abstract fun inject(activity: FeedActivity);


}
