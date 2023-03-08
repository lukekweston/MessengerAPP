package weston.luke.messengerappmvvm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.dao.LoggedInUserDao
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.database.entities.Message

//Version will need to be bumped after every database change
//Keeps track of migrations
@Database(entities = [LoggedInUser::class, Conversation::class, Message::class], version = 11)
@TypeConverters(Converters::class)
abstract class MessengerAppMVVMDatabase : RoomDatabase() {

    abstract fun loggedInUserDao(): LoggedInUserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao


    companion object {
        //Needs to be a singleton as there should only ever be one instance max of the database
        @Volatile
        private var INSTANCE: MessengerAppMVVMDatabase? = null

        fun getDatabase(context: Context): MessengerAppMVVMDatabase {
//            If INSTANCE is not null, then return it
//            If it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessengerAppMVVMDatabase::class.java,
                    "messenger_app_mvvm_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
//                return the instance
                instance
            }
        }

    }
}




