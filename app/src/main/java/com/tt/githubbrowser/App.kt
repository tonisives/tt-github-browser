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
        viewModel { MainViewModel(get(), get(), get()) }
        viewModel { LoginViewModel(get(), get()) }
        viewModel { RepoListViewModel(get(), get(), get()) }
        viewModel { RepoListItemViewModel(get()) }
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
    }

    companion object {
        lateinit var instance: App private set
        lateinit var context: Context private set
    }
}