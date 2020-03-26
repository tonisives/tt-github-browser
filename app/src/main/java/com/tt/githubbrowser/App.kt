package com.tt.githubbrowser

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.tt.githubbrowser.dao.AppDatabase
import com.tt.githubbrowser.dao.SharedPreferencesDao
import com.tt.githubbrowser.network.client.AuthClient
import com.tt.githubbrowser.network.client.RepoClient
import com.tt.githubbrowser.repository.AuthRepository
import com.tt.githubbrowser.repository.RepoRepository
import com.tt.githubbrowser.repository.UserRepository
import com.tt.githubbrowser.ui.RepoListItemViewModel
import com.tt.githubbrowser.ui.RepoListViewModel
import com.tt.githubbrowser.ui.LoginViewModel
import com.tt.githubbrowser.ui.MainViewModel
import com.tt.githubbrowser.util.AppExecutors
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import java.util.concurrent.Executors

class App : Application() {
    private val appModule = module {
        single {
            AppExecutors(
                Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(3),
                AppExecutors.MainThreadExecutor()
            )
        }
        single {
            Room.databaseBuilder(this@App, AppDatabase::class.java, "app-db")
                .fallbackToDestructiveMigration()
                .build()
        }
        single { SharedPreferencesDao(context) }
    }

    private val userModule = module {
        single {
            // ^^ the appModule should have the AppDatabase created
            get<AppDatabase>().userDao()
        }

        single { AuthClient(context) }

        single { UserRepository(get(), get(), get()) }

        single { AuthRepository(get(), get(), get(), get()) }
    }

    private val repoModule = module {
        single {
            // ^^ the appModule should have the AppDatabase created
            get<AppDatabase>().repoDao()
        }

        single { RepoClient(context, get()) }

        single { RepoRepository(get(), get(), get()) }
    }

    private val viewModelModule = module {
        viewModel { (handle: SavedStateHandle) -> MainViewModel(handle, get(), get()) }
        viewModel { (handle: SavedStateHandle) -> LoginViewModel(handle, get()) }
        viewModel { (handle: SavedStateHandle) -> RepoListViewModel(handle, get(), get()) }
        viewModel { (handle: SavedStateHandle) -> RepoListItemViewModel(handle) }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        instance = this
        context = applicationContext

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, viewModelModule, userModule, repoModule))
        }

        hideTitleBar()
    }

    private fun hideTitleBar() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityDestroyed(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
                (activity as AppCompatActivity).supportActionBar?.hide(); //hide the title bar
                activity.actionBar?.hide()

                activity.window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }
            }

            override fun onActivityResumed(activity: Activity) {}
        })
    }

    companion object {
        lateinit var instance: App private set
        lateinit var context: Context private set
    }
}