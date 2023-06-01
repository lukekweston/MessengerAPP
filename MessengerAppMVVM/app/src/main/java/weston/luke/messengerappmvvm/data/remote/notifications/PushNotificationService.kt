package weston.luke.messengerappmvvm.data.remote.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.dao.FriendDao
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.database.entities.*
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationAndFriendsActivity
import weston.luke.messengerappmvvm.ui.messages.activity.MessagesActivity
import weston.luke.messengerappmvvm.util.Constants
import java.time.LocalDateTime
import javax.inject.Inject


@AndroidEntryPoint
class PushNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var messageDao: MessageDao
    @Inject
    lateinit var friendDao: FriendDao
    @Inject
    lateinit var conversationDao: ConversationDao

    //Value to keep track of the notification id
    companion object {
        private var lastNotificationId = 0
    }


    //This receives a notifaction and decides how to display it on the screen
    //Only works for when the app is in the foreground
    override fun onMessageReceived(message: RemoteMessage) {
//        messageDao = MessengerAppMVVMDatabase.getDatabase(applicationContext).messageDao()
//        friendDao = MessengerAppMVVMDatabase.getDatabase(applicationContext).friendDao()
//        conversationDao = MessengerAppMVVMDatabase.getDatabase(applicationContext).conversationDao()

        when (message.data.get("type")) {
            "newMessage" -> newMessageReceived(
                Message(
                    messageId = message.data.get("id")!!.toInt(),
                    userId = message.data.get("userId")!!.toInt(),
                    userName = message.data.get("usernameSending")!!,
                    conversationId = message.data.get("conversationId")!!.toInt(),
                    timeSent = LocalDateTime.now(),
                    status = MessageStatus.SUCCESS,
                    message = message.data.get("textMessage")!!
                )
            )
            "newImageMessage" -> newMessageReceived(
                Message(
                    messageId = message.data.get("id")!!.toInt(),
                    userId = message.data.get("userId")!!.toInt(),
                    userName = message.data.get("usernameSending")!!,
                    conversationId = message.data.get("conversationId")!!.toInt(),
                    timeSent = LocalDateTime.now(),
                    status = MessageStatus.IMAGE_READY_TO_GET_FROM_API,
                    message = ""
                )
            )

            "friendRequest" -> friendRequest(message.data)
            "friendStatusUpdate" -> friendStatusUpdate(message.data)
        }

        super.onMessageReceived(message)
    }

    fun newMessageReceived(message: Message) {


        //Insert the message into the room database
        insertMessage(message)


        //Create the notification
        val CHANNEL_ID: String = "NEW_MESSAGE_NOTIFICATION"

        var channel: NotificationChannel = NotificationChannel(
            CHANNEL_ID, "New message notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        //Intent for the notification to go to
        val intent = Intent(applicationContext, MessagesActivity::class.java)
        intent.putExtra(Constants.CONVERSATION_ID, message.conversationId)
        intent.putExtra(Constants.OPEN_CONVERSATION_FROM_NOTIFICATION, true)
        //No history flag to stop a loop being created
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, FLAG_MUTABLE)

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(
            this, CHANNEL_ID
        )
            //Todo make the title/message dynamic on if the notification is coming from a group conversation or not
            .setContentTitle(message.userName)
            .setContentText(if (message.status == MessageStatus.IMAGE_READY_TO_GET_FROM_API) "Sent you an image!" else message.message)
            .setSmallIcon(R.drawable.ic_banta_conversation_icon)
            .setContentIntent(pendingIntent)
            //Goes away when user taps on it
            .setAutoCancel(true)


        //Build and display the notification
        NotificationManagerCompat.from(this)
            //Set the notification id to be the same as the last message id
            //This is so it can be found and dismissed in the activity
            .notify(message.messageId!!, notification.build())
    }


    private fun insertMessage(message: Message) {
        GlobalScope.launch {
            messageDao.insertMessage(
                message
            )
        }
    }

    private fun insertFriend(friend: Friend) {
        GlobalScope.launch {
            friendDao.insertFriend(
                friend
            )
        }
    }

    private fun updateFriendStatusAndConversationIdByFriendId(friendshipStatus: FriendshipStatus, conversationId: Int?, friendId: Int) {
        GlobalScope.launch {
            friendDao.updateFriendStatusForFriendId(friendshipStatus,conversationId, friendId)
        }
    }

    private fun insertConversation(conversation: Conversation) {
        GlobalScope.launch {
            conversationDao.insertConversation(
                conversation
            )
        }
    }

    private fun deleteConversationAndFriendByFriendId(friendId: Int) {
        GlobalScope.launch {
            conversationDao.deletePrivateConversationByFriendshipId(friendId)
            friendDao.deleteByFriendId(
                friendId
            )
        }
    }


    private fun friendRequest(data: MutableMap<String, String>) {

        val conversationId = if(data.get("conversationId") != null) data.get("conversationId")!!.toInt() else null

        insertFriend(
            Friend(
                friendId = data.get("fromUserId")!!.toInt(),
                friendUserName = data.get("fromUserName")!!,
                friendStatus = FriendshipStatus.Pending,
                privateConversationId = conversationId
            )
        )

        if(conversationId != null){
            insertConversation(Conversation(conversationId, data.get("conversationName")!!, null))
        }


        //Create the notification
        val CHANNEL_ID: String = "NEW_FRIEND_REQUEST_NOTIFICATION"

        var channel: NotificationChannel = NotificationChannel(
            CHANNEL_ID, "New friend notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        //Intent for the notification to go to
        val intent = Intent(applicationContext, ConversationAndFriendsActivity::class.java)
        intent.putExtra(Constants.GOTO_FRIEND_FRAGMENT, true)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, FLAG_IMMUTABLE)

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(
            this, CHANNEL_ID
        )

            .setContentTitle(data.get("title"))
            .setContentText(data.get("body"))
            .setSmallIcon(R.drawable.ic_banta_conversation_icon)
            .setContentIntent(pendingIntent)
            //Goes away when user taps on it
            .setAutoCancel(true)


        lastNotificationId += 1
        //Build and display the notification
        NotificationManagerCompat.from(this)
            //Set the notification id to be unique
            //This is so it can be found and dismissed in the activity
            .notify(lastNotificationId, notification.build())

    }

    fun friendStatusUpdate(data: MutableMap<String, String>) {

        val friendId = data.get("fromUserId")!!.toInt()
        val newFriendshipStatus = FriendshipStatus.valueOf(data.get("status")!!.toString())
        val conversationId = if(data.get("conversationId").isNullOrEmpty()) null else data.get("conversationId")!!.toInt()

        if(conversationId != null){
            insertConversation(Conversation(conversationId, data.get("conversationName")!!, null))
        }


        //If friendship status is declined or removed, then remove the relationship in the local database
        if (newFriendshipStatus == FriendshipStatus.Declined || newFriendshipStatus == FriendshipStatus.Removed
        ) {
            deleteConversationAndFriendByFriendId(friendId)
        } else {
            //Update the existing friend item
            updateFriendStatusAndConversationIdByFriendId(newFriendshipStatus, conversationId, friendId)
        }

        //Only notify the user on friend acceptance
        if (FriendshipStatus.valueOf(data.get("status")!!.toString()) == FriendshipStatus.Friends) {

            //Create the notification
            val CHANNEL_ID: String = "NEW_FRIEND_REQUEST_NOTIFICATION"

            var channel: NotificationChannel = NotificationChannel(
                CHANNEL_ID, "New friend accept notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            //Intent for the notification to go to
            val intent = Intent(applicationContext, ConversationAndFriendsActivity::class.java)
            intent.putExtra(Constants.GOTO_FRIEND_FRAGMENT, true)
            val pendingIntent =
                PendingIntent.getActivity(applicationContext, 0, intent, FLAG_IMMUTABLE)

            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
            val notification: Notification.Builder = Notification.Builder(
                this, CHANNEL_ID
            )

                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setSmallIcon(R.drawable.ic_banta_conversation_icon)
                .setContentIntent(pendingIntent)
                //Goes away when user taps on it
                .setAutoCancel(true)


            lastNotificationId += 1
            //Build and display the notification
            NotificationManagerCompat.from(this)
                //Set the notification id to be unique
                //This is so it can be found and dismissed in the activity
                .notify(lastNotificationId, notification.build())
        }

    }
}