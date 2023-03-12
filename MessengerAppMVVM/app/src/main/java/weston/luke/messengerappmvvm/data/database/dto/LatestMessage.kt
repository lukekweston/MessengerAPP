package weston.luke.messengerappmvvm.data.database.dto

import java.time.LocalDateTime


data class LatestMessage(
    val id: Int,
    val userName: String?,
    val message: String?,
    val latestTime: LocalDateTime?,
    val conversationId: Int,
    val pathToSavedLowRes: String?
)