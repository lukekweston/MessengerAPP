package weston.luke.messengerappmvvm.data.remote.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.data.database.MessengerAppMVVMDatabase
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.database.entities.SentStatus
import java.time.LocalDateTime

class PushNotificationService : FirebaseMessagingService() {

    lateinit var messageDao: MessageDao

    //This receives a notifaction and decides how to display it on the screen
    //Only works for when the app is in the foreground
    override fun onMessageReceived(message: RemoteMessage) {
        messageDao = MessengerAppMVVMDatabase.getDatabase(applicationContext).messageDao()
    applicationContext
        when (message.data.get("type")){
            "newMessage" -> newMessageReceived(message)
        }

//        var title: String? = message.notification?.title
//        var text: String? = message.notification?.body
//
//
//        //todo
//        //Get the current opened activity
//        insertMessage("hello from the back")
//
//
//        //Check what the title is, use this to determine what database update to do
//        when(title){
//            "message" -> insertMessage(text!!)
//            //"messageUpdate" -> update existing message
//            //"messageDelete" -> delete a message
//            //"conversationAdd" -> add new conversation
//            //"conversationRename" -> rename conversation
//        }

//Change the notification importance based on what the notification is and where in the app the user is
/*
        IMPORTANCE_UNSPECIFIED: This importance level is used as the default for notifications that do not have a specified importance. Notifications with this level will be treated as lower priority notifications.

        IMPORTANCE_MIN: Notifications with this level will not appear in the status bar. They may still appear in the notification shade, but they will not generate a sound or vibration.

        IMPORTANCE_LOW: Notifications with this level will not appear in the status bar, but they will appear in the notification shade. They will not generate a sound, but they may generate a vibration.

        IMPORTANCE_DEFAULT: Notifications with this level will appear in the status bar and notification shade, and may generate a sound or vibration depending on the user's settings.

        IMPORTANCE_HIGH: Notifications with this level will appear in the status bar and notification shade, and will generate a sound and/or vibration by default.

        IMPORTANCE_MAX: Notifications with this level will appear in the status bar and notification shade, and will generate a sound and/or vibration at a higher priority than IMPORTANCE_HIGH notifications.

*/



        super.onMessageReceived(message)
    }

    fun newMessageReceived(message: RemoteMessage){



        insertMessage(
            Message(
                messageId = message.data.get("id")!!.toInt(),
                userId = message.data.get("userId")!!.toInt(),
                userName = message.data.get("usernameSending")!!,
                conversationId = message.data.get("conversationId")!!.toInt(),
                timeSent = LocalDateTime.now(),
                status = SentStatus.SUCCESS,
                message = message.data.get("textMessage")!!
            )
        )



        //Create the notification
        val CHANNEL_ID: String = "HEADS_UP_NOTIFICATION"

        var channel: NotificationChannel = NotificationChannel(
            CHANNEL_ID, "Heads up notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(
            this, CHANNEL_ID
        )
            .setContentTitle(message.data.get("usernameSending"))
            .setContentText(message.data.get("textMessage"))
            .setSmallIcon(R.drawable.ic_conversations)
            //Goes away when user taps on it
            .setAutoCancel(true)

        //Build and display the notification
        NotificationManagerCompat.from(this).notify(1, notification.build())
    }


    private fun insertMessage(message: Message) {
        GlobalScope.launch {
            Log.d("inserting message?", "Does this work")
            messageDao.insertMessage(
                message
            )
        }
    }
}