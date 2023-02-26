package weston.luke.messengerappmvvm.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime


@Parcelize
@Entity(tableName = "conversation")
data class Conversation(
    @PrimaryKey val conversationId: Int,
    @ColumnInfo val conversationName: String?,
    @ColumnInfo val lastUpdatedDateTime: LocalDateTime?,
) :Parcelable
