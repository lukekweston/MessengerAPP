package weston.luke.messengerappmvvm.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import weston.luke.messengerappmvvm.data.remote.api.CacheInterceptor
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.util.Constants
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context): OkHttpClient{
        val cacheSize = (100 * 1024 * 1024).toLong() // 100 MB
        val cacheDirectory = File(context.cacheDir, "http-cache")
        val cache = Cache(cacheDirectory, cacheSize)

        val cacheInterceptor = Interceptor { chain ->
            val request = chain.request()
            //Only cache the full res image endpoint
            if (request.url.toString().contains(Constants.API_ENDPOINT_GET_FULL_RES_IMAGE_FOR_MESSAGE)) {
                val response = chain.proceed(request)
                response.newBuilder()
                    .header("Cache-Control", "max-age=3600") // Cache for 1 hour
                    .build()
            } else {
                chain.proceed(request)
            }
        }


        val logging = HttpLoggingInterceptor()
        return OkHttpClient.Builder().apply {
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    //todo Futrue, add api key?
                    // builder.header("X-Api-key", Constants.apiKey)
                    return@Interceptor chain.proceed(builder.build())
                }
            )
                .cache(cache)
                .addInterceptor(cacheInterceptor)
                .addNetworkInterceptor(CacheInterceptor())

            logging.level = HttpLoggingInterceptor.Level.BODY
            addNetworkInterceptor(logging)
        }
            //.addInterceptor(NetworkConnectionInterceptor(context))
            //Time out the api calls after 30 seconds of no response
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): MessengerAPIService{
        return retrofit.create(MessengerAPIService::class.java)
    }


}