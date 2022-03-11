package durak.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Qualifier
@Retention(BINARY)
annotation class IODispatcher

@Qualifier
@Retention(BINARY)
annotation class DefaultDispatcher

@InstallIn(SingletonComponent::class)
@Module
class DispatchersModule {
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher() = Default
    @Provides
    @IODispatcher
    fun provideIODispatcher() = IO
}
