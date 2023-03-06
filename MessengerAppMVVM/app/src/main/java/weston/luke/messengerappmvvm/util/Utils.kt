package weston.luke.messengerappmvvm.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import weston.luke.messengerappmvvm.data.remote.request.LogoutRequest
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
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



    //Accessible everywhere method to logout the currently logged in user
    suspend fun logoutUser(
        loggedInUserRepository: LoggedInUserRepository,
        conversationRepository: ConversationRepository,
        messageRepository: MessageRepository,
        loggedInUserId: Int,
        loggedInUserName: String
    ) {


        //Delete the users fcm_reg_token from server
        loggedInUserRepository.logoutUser(
            LogoutRequest(
                userId = loggedInUserId,
                userName = loggedInUserName
            )
        )

        //Delete users data locally
        loggedInUserRepository.deleteUserFromLocalDatabase()
        conversationRepository.deleteConversationData()
        messageRepository.deleteAllMessages()


    }
}