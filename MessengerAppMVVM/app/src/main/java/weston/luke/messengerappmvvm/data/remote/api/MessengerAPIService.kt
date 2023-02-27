package weston.luke.messengerappmvvm.data.remote.api

import io.reactivex.rxjava3.core.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.response.ConversationResponse
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.data.remote.response.MessageResponse
import weston.luke.messengerappmvvm.data.remote.response.UserResponse
import weston.luke.messengerappmvvm.util.Constants
import java.util.concurrent.TimeUnit


class MessengerAPIService {


    private val logging = HttpLoggingInterceptor()
    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                //todo Futrue, add api key?
                // builder.header("X-Api-key", Constants.apiKey)
                return@Interceptor chain.proceed(builder.build())
            }
        )
        logging.level = HttpLoggingInterceptor.Level.BODY
        addNetworkInterceptor(logging)
        }
        //Time out the api calls after 10 seconds of no response
        .callTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()


    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(httpClient)
        .build()
        .create(messengerAPIInterface::class.java)

    fun loginUser(loginRequest: LoginRequest): Single<LoginResponse> {
        return api.loginUser(loginRequest)
    }

    fun getAllConversationsForUser(userId: Int): Single<ConversationResponse> {
        return api.getAllConversationsForUser(userId)
    }

    fun getAllMessagesForUser(userId: Int): Single<MessageResponse>{
        return api.getAllMessagesForUser(userId)
    }
}