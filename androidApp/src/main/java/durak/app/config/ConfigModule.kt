@file:JvmName("ConfigStoreAndroid")

package durak.app.config

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ConfigModule {
    @Provides
    @Singleton
    fun configStore(@ApplicationContext context: Context) = DataStoreFactory.create(ConfigSerializer) { context.dataStoreFile("config") }
}
