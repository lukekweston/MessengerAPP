package weston.luke.messengerappmvvm.data.remote.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import weston.luke.messengerappmvvm.R

class PushNotificationService : FirebaseMessagingService() {

    //This receives a notifaction and decides how to display it on the screen
    override fun onMessageReceived(message: RemoteMessage) {
        var title: String? = message.notification?.title
        var text: String? = message.notification?.body

        val CHANNEL_ID: String = "HEADS_UP_NOTIFICATION"

        var channel: NotificationChannel = NotificationChannel(
            CHANNEL_ID, "Heads up notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(
            this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_conversations)
                //Goes away when user taps on it
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(1, notification.build())


        Conversation


        super.onMessageReceived(message)
    }
}