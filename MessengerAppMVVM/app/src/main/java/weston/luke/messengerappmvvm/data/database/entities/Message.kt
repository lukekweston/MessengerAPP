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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: Int? = null,
    val userId: Int,
    val conversationId: Int,
    val userName: String,
    val message: String,
    val timeSent: LocalDateTime,
    val timeUpdated: LocalDateTime? = null,
    val status: SentStatus,
    val image: ByteArray? = null
) : Parcelable


enum class SentStatus {
    SUCCESS,
    CREATED,
}