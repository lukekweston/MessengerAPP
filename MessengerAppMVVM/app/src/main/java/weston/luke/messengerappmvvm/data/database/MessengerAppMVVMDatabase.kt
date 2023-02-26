package weston.luke.messengerappmvvm.data.database

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.dao.LoggedInUserDao
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

//Version will need to be bumped after every database change
//Keeps track of migrations
@Database(entities = [LoggedInUser::class, Conversation::class], version = 4)
@TypeConverters(Converters::class)
abstract class MessengerAppMVVMDatabase : RoomDatabase() {

    abstract fun loggedInUserDao(): LoggedInUserDao
    abstract fun conversationDao(): ConversationDao


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




