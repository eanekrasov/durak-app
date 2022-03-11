@file:Suppress("DEPRECATION", "unused")

package durak.app

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.Executors.newSingleThreadExecutor

@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineScopeRule : TestWatcher() {
    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)
    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) = scope.runBlockingTest { block() }

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        scope.cleanupTestCoroutines()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineDispatcherRule : TestWatcher() {
    private val dispatcher = TestCoroutineDispatcher()
    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) = dispatcher.runBlockingTest { block() }

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineContextRule(private val dispatcher: ExecutorCoroutineDispatcher) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        dispatcher.close()
    }
}

@DelicateCoroutinesApi
fun testCoroutineContextRule() = TestCoroutineContextRule(newSingleThreadContext("UI thread"))
fun singleThreadExecutorRule() = TestCoroutineContextRule(newSingleThreadExecutor().asCoroutineDispatcher())
fun testCoroutineScopeRule() = TestCoroutineScopeRule()
fun testCoroutineDispatcherRule() = TestCoroutineDispatcherRule()
