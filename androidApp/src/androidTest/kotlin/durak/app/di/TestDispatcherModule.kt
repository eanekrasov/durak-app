@file:Suppress("unused")

package durak.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.Dispatchers.Unconfined

@TestInstallIn(components = [SingletonComponent::class], replaces = [DispatchersModule::class])
@Module class TestDispatchersModule {
    @DefaultDispatcher @Provides fun defaultDispatcher() = Unconfined
    @IODispatcher @Provides fun ioDispatcher() = Unconfined
}
