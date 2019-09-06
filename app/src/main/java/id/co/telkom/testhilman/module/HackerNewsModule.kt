package id.co.telkom.testhilman.module

import dagger.Module
import dagger.Provides
import hackernews.api.HackerNewAPI
import hackernews.api.config.HackerNewsConfig
import hackernews.api.entity.service.FeedService
import hackernews.api.entity.service.spi.FeedServiceImpl

@Module
class HackerNewsModule {
    @Provides
    fun mHackerNewsService(): HackerNewsConfig {
        return HackerNewsConfig();
    }

    @Provides
    fun mHackerNewsService2(config: HackerNewsConfig): FeedService {
        return FeedServiceImpl(config);
    }
}