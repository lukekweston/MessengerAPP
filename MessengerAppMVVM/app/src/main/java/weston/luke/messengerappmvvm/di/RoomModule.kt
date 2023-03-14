package weston.luke.messengerappmvvm.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import weston.luke.messengerappmvvm.data.database.MessengerAppMVVMDatabase
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.dao.FriendDao
import weston.luke.messengerappmvvm.data.database.dao.LoggedInUserDao
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideMessengerAppMVVMDb(@ApplicationContext context: Context): MessengerAppMVVMDatabase {
        return Room
            .databaseBuilder(
                context,
                MessengerAppMVVMDatabase::class.java,
                MessengerAppMVVMDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesConversationDAO(messengerAppMVVMDatabase: MessengerAppMVVMDatabase): ConversationDao{
        return messengerAppMVVMDatabase.conversationDao()
    }

    @Singleton
    @Provides
    fun providesFriendDAO(messengerAppMVVMDatabase: MessengerAppMVVMDatabase): FriendDao {
        return messengerAppMVVMDatabase.friendDao()
    }

    @Singleton
    @Provides
    fun providesLoggedInUserDAO(messengerAppMVVMDatabase: MessengerAppMVVMDatabase): LoggedInUserDao {
        return messengerAppMVVMDatabase.loggedInUserDao()
    }

    @Singleton
    @Provides
    fun providesMessageDAO(messengerAppMVVMDatabase: MessengerAppMVVMDatabase): MessageDao {
        return messengerAppMVVMDatabase.messageDao()
    }

}