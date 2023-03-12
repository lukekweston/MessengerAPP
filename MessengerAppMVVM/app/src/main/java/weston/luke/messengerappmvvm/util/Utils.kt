package weston.luke.messengerappmvvm.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.time.format.DateTimeFormatter
import java.util.*


object Utils {
    @SuppressLint("ConstantLocale")
    val formatDayMonthHourMin: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.getDefault())

    @SuppressLint("ConstantLocale")
    val formatHourMin: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    @SuppressLint("ConstantLocale")
    val formatDayMonth: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())


    fun displayOkAlertDialog(context: Context, message: String) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    fun getBitmapFromByteArray(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun createAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveText: String = "Ok",
        negativeText: String = "Cancel",
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {}
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText) { dialog, which ->
            onPositiveClick()
            dialog.dismiss()
        }
        builder.setNegativeButton(negativeText) { dialog, which ->
            onNegativeClick()
            dialog.dismiss()
        }
        return builder.create()
    }

}