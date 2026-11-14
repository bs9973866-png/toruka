package com.toruka.launcher.di

import android.content.Context
import androidx.room.Room
import com.toruka.launcher.data.api.*
import com.toruka.launcher.data.local.TorukaDatabase
import com.toruka.launcher.data.local.ProfileDao
import com.toruka.launcher.data.local.AccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideMojangApi(okHttpClient: OkHttpClient): MojangApi {
        return Retrofit.Builder()
            .baseUrl(MojangApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MojangApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDownloadApi(okHttpClient: OkHttpClient): DownloadApi {
        return Retrofit.Builder()
            .baseUrl(MojangApi.RESOURCES_URL)
            .client(okHttpClient)
            .build()
            .create(DownloadApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMicrosoftAuthApi(okHttpClient: OkHttpClient): MicrosoftAuthApi {
        return Retrofit.Builder()
            .baseUrl(MicrosoftAuthApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MicrosoftAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TorukaDatabase {
        return Room.databaseBuilder(
            context,
            TorukaDatabase::class.java,
            "toruka_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProfileDao(database: TorukaDatabase): ProfileDao = database.profileDao()

    @Provides
    fun provideAccountDao(database: TorukaDatabase): AccountDao = database.accountDao()

    @Provides
    @Singleton
    @GameDirectory
    fun provideGameDirectory(@ApplicationContext context: Context): File {
        val dir = File(context.getExternalFilesDir(null), ".minecraft")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}
