package weston.luke.messengerappmvvm.ui.messages

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivityMessagesBinding
import weston.luke.messengerappmvvm.util.Constants
import weston.luke.messengerappmvvm.util.toast
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*


//Got messaging UI from here - modified a lot from this
//https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui

class MessagesActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMessagesBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private var firstLoad: Boolean = true

    private lateinit var photoFile: File
    private lateinit var photoURI: Uri

    private val mMessageViewModel: MessagesViewModel by viewModels {
        MessagesViewModelFactory(
            (application as MessengerAppMVVMApplication).loggedInUserRepository,
            (application as MessengerAppMVVMApplication).conversationRepository,
            (application as MessengerAppMVVMApplication).messageRepository
        )
    }

    private var conversationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //Get conversationId passed in when starting activity
        conversationId = intent.getIntExtra(Constants.CONVERSATION_ID, -1)
        if (conversationId == -1) {
            this.toast("Error getting conversation")
            onBackPressed()
        }

        //Set up support bar
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Set the support bars name
        lifecycleScope.launch {
            mMessageViewModel.getConversation(conversationId).collect { conversation ->
                supportActionBar!!.title = conversation.conversationName
            }
        }


        //Set up the displaying messages recyclerview
        messagesAdapter = MessagesAdapter(this)
        messagesRecyclerView = mBinding.recyclerGchat
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messagesAdapter
        (messagesRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true

        //Load the data in the viewModel
        mMessageViewModel.loadData(conversationId)

        //Update the recyclerview when the messages are received/updated
        mMessageViewModel.loggedInUserAndMessages.observe(this) { (messages, loggedInUserId) ->
            messagesAdapter.setData(loggedInUserId = loggedInUserId, messages = messages)
            //On initial load scroll the recyclerview to the bottom when there are messages loaded
            if (firstLoad && messages.isNotEmpty()) {
                messagesRecyclerView.scrollToPosition(messages.size - 1)
            }

            //Automatically dismiss the notification based on the message id of the last message added
            //to the room database
            //Need to do a null check on messageId incase there is an undelivered message
            if (messages.isNotEmpty() && messages.last().messageId != null) {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(messages.last().messageId!!)
            }
        }

        mMessageViewModel.toastMessageToDisplay.observe(this) { toastMessageToDisplay ->
            if (toastMessageToDisplay != null) {
                toast(toastMessageToDisplay)
                //Todo, set up a way to resend the message
                //And make sure the message is displayed differently
            }
        }


        mBinding.buttonSend.setOnClickListener {

            //close the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mBinding.etMessage.windowToken, 0)

            val textMessage = mBinding.etMessage.text.toString()

            mMessageViewModel.sendMessage(textMessage, conversationId)

            //Clear the textMessage
            mBinding.etMessage.text.clear()

            //Load data again to update recycler View
            // mMessageViewModel.loadData(conversationId)
        }


        mBinding.ivOpenCamera.setOnClickListener {
            openCamera()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


    fun openCamera() {
        Dexter.withContext(this@MessagesActivity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                // Let only runs the code when report is not null - good to know
                report?.let {
                    // Checks all permissions above are granted
                    if (report.areAllPermissionsGranted()) {

                        // Create a file to store the photo
                        photoFile = createImageFile()

                        // Get the URI of the photo file
                        photoURI = FileProvider.getUriForFile(this@MessagesActivity,
                            "weston.luke.messengerappmvvm.fileprovider",
                            photoFile)

                        // Need to specify class as in listener
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAMERA)

                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }
        ).onSameThread().check()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            //data?.extras?.let {

                // Get the image data from the file
                val imageData = FileInputStream(photoFile).use { it.readBytes() }

                //Save the image to messages table
                mMessageViewModel.sendMessage("", conversationId, imageData)

            //}
        }
    }












//    Display an alert saying that the user doesn't have the required permissions set
//    On positive click go to phone settings to be able to enable permissions - dont need to dismiss this as going to a new display, settings
//    Negative click - dismiss
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions required fot this feature. It can ben enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
//                    Create an intent to go towards phone settings - specifically to application details settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    use the package name of this app to be able to get to the specific application settings for this app
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
//                    Start the settings
                    startActivity(intent)
                }
//                Catch not found, print stack trace
                catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    private fun createImageFile(): File {
        // Create a unique file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        // Get the directory where the file will be stored
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Create the file and return its path
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
    }


    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2

        private const val IMAGEDIRECTORY = "FavDishImages"
    }

}