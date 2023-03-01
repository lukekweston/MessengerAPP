package weston.luke.messengerappmvvm.util

import android.annotation.SuppressLint
import java.time.format.DateTimeFormatter
import java.util.*


object Utils {
    @SuppressLint("ConstantLocale")
    val formatDayMonthHourMin: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.getDefault())
    @SuppressLint("ConstantLocale")
    val formatHourMin: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
}