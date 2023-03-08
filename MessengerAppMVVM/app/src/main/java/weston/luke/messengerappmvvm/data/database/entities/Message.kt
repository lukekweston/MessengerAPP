package weston.luke.messengerappmvvm.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime


@Parcelize
@Entity(tableName = "message")
data class Message(
    //Duplicate id here, message id and id as messages will be created in room database before being
    //posted to database (for holding created messages if the post fails)
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val messageId: Int? = null,
    val userId: Int,
    val conversationId: Int,
    val userName: String,
    val message: String,
    val timeSent: LocalDateTime,
    val timeUpdated: LocalDateTime? = null,
    var status: MessageStatus,
    var pathToSavedLowRes: String? = null,
    val pathToSavedHighRes: String? = null
) : Parcelable


enum class MessageStatus {
    //Image is successfully saved in db
    SUCCESS,
    //Image is created but has not been sent to server successfully
    CREATED,
    //Notification of image received, ready to download low res version
    IMAGE_READY_TO_GET_FROM_API,
    //Successfully have downloaded the low res image, ready to get high res when needed
    SUCCESS_LOW_RES_IMAGE_ONLY
}