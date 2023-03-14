package weston.luke.messengerappmvvm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.dao.FriendDao
import weston.luke.messengerappmvvm.data.database.dao.LoggedInUserDao
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.repository.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideConversationRepository(
        conversationDao: ConversationDao,
        apiService: MessengerAPIService
    ): ConversationRepository {
        return ConversationRepository(conversationDao, apiService)
    }


    @Singleton
    @Provides
    fun provideFriendRepository(
        friendDao: FriendDao,
        conversationRepository: ConversationRepository,
        apiService: MessengerAPIService
    ): FriendRepository {
        return FriendRepository(friendDao, conversationRepository, apiService)
    }


    @Singleton
    @Provides
    fun provideLoggedInUserRepository(
        loggedInUserDao: LoggedInUserDao,
        apiService: MessengerAPIService
    ): LoggedInUserRepository {
        return LoggedInUserRepository(loggedInUserDao, apiService)
    }

    @Singleton
    @Provides
    fun provideMessageRepository(
        messageDao: MessageDao,
        apiService: MessengerAPIService
    ): MessageRepository {
        return MessageRepository(messageDao, apiService)
    }

    @Singleton
    @Provides
    fun providesParentRepository(
        conversationRepository: ConversationRepository,
        loggedInUserRepository: LoggedInUserRepository,
        messageRepository: MessageRepository,
        friendRepository: FriendRepository
    ): ParentRepository{
        return ParentRepository(conversationRepository, loggedInUserRepository, messageRepository, friendRepository)
    }

}