package weston.luke.messengerappmvvm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import weston.luke.messengerappmvvm.data.database.dao.LoggedInUserDao
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser


@Database(entities = [LoggedInUser::class], version = 1)
abstract class MessengerAppMVVMDatabase : RoomDatabase() {

    abstract fun loggedInUserDao(): LoggedInUserDao


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

