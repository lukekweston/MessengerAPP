package weston.luke.messengerappmvvm.data.database.dto

import java.time.LocalDateTime

data class LatestMessage(val userName: String?, val message: String?, val latestTime: LocalDateTime?)
