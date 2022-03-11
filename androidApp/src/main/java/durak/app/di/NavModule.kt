package durak.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.multibindings.IntoSet
import durak.app.R.string.*
import durak.app.client.clientGraph
import durak.app.config.configGraph
import durak.app.custom.customGraph
import durak.app.server.serverGraph
import durak.app.utils.NavFeature

@InstallIn(ActivityComponent::class)
@Module
internal object NavModule {
    @ActivityScoped
    @Provides
    @IntoSet
    fun serverNav() = NavFeature("server", menu_server, 2) { serverGraph("server", it) }
    @ActivityScoped
    @Provides
    @IntoSet
    fun clientNav() = NavFeature("client", menu_client, 4) { clientGraph("client", it) }
    @ActivityScoped
    @Provides
    @IntoSet
    fun customNav() = NavFeature("custom", menu_custom, 6) { customGraph("custom", it) }
    @ActivityScoped
    @Provides
    @IntoSet
    fun configNav() = NavFeature("config", menu_config, 8) { configGraph("config", it) }
}
