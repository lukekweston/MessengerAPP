package weston.luke.messengerappmvvm.util

import java.time.format.DateTimeFormatter
import java.util.*


object utils {
    val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.getDefault())
}