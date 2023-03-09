package weston.luke.messengerappmvvm.data.remote.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.request.LogoutRequest
import weston.luke.messengerappmvvm.data.remote.request.MessageSendRequest
import weston.luke.messengerappmvvm.data.remote.request.fcmRegTokenCheckRequest
import weston.luke.messengerappmvvm.data.remote.response.*
import weston.luke.messengerappmvvm.util.Constants
import java.util.concurrent.TimeUnit


class MessengerAPIService(private val context: Context){


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
        //.addInterceptor(NetworkConnectionInterceptor(context))
        //Time out the api calls after 10 seconds of no response
        .callTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()


    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(httpClient)
        .build()
        .create(MessengerAPIInterface::class.java)

    suspend fun loginUser(loginRequest: LoginRequest): LoginResponse {
        return api.loginUser(loginRequest)
    }

    suspend fun logoutUser(logoutRequest: LogoutRequest): SuccessResponse {
        return api.logoutUser(logoutRequest)
    }

    suspend fun checkFcmRegToken(fcmRegTokenCheckRequest: fcmRegTokenCheckRequest): SuccessResponse {
        return api.checkFcmRegToken(fcmRegTokenCheckRequest)
    }

    suspend fun getAllConversationsForUser(userId: Int): ConversationResponse {
        return api.getAllConversationsForUser(userId)
    }

    suspend fun getAllMessagesForUser(userId: Int): MessageResponseList{
        return api.getAllMessagesForUser(userId)
    }

    suspend fun sendMessage(messageRequest: MessageSendRequest) : MessageResponse {
        return api.sendMessage(messageRequest)
    }

    suspend fun getLowResImageForMessage(messageId: Int): ImageResponse{
        return api.getLowResImageForMessage(messageId)
    }

    suspend fun getFullResImageForMessage(messageId: Int): ImageResponse{
        return api.getFullResImageForMessage(messageId)
    }
}