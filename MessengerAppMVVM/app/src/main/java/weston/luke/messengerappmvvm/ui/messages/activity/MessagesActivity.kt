package weston.luke.messengerappmvvm.ui.messages.activity

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.databinding.ActivityMessagesBinding
import weston.luke.messengerappmvvm.ui.messages.MessagesAdapter
import weston.luke.messengerappmvvm.ui.messages.MessagesViewModel
import weston.luke.messengerappmvvm.util.*
import weston.luke.messengerappmvvm.util.Constants.IMAGE_BY_USERNAME
import weston.luke.messengerappmvvm.util.Constants.IMAGE_FILE_NAME
import weston.luke.messengerappmvvm.util.Constants.IMAGE_ID
import java.io.File
import java.io.FileInputStream


//Got messaging UI from here - modified a lot from this
//https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui

@AndroidEntryPoint
class MessagesActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMessagesBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private var firstLoad: Boolean = true

    private lateinit var photoFile: File
    private lateinit var photoURI: Uri

    private val mMessageViewModel: MessagesViewModel by viewModels()

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
        messagesAdapter = MessagesAdapter(this) { messageId, imageByUser, imageFileName ->
            val intent = Intent(this, FullSizeImageActivity::class.java)
            intent.putExtra(IMAGE_ID, messageId)
            intent.putExtra(IMAGE_BY_USERNAME, imageByUser)
            intent.putExtra(IMAGE_FILE_NAME, imageFileName)
            startActivity(intent)
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        messagesRecyclerView = mBinding.recyclerviewMessages
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messagesAdapter
        (messagesRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true


        //Load/get the data in the viewModel
        messagesRecyclerView.hide()
        mBinding.loadingSpinner.show()
        mMessageViewModel.loadData(conversationId, this)
        messagesRecyclerView.show()
        mBinding.loadingSpinner.hide()


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

        }

        mBinding.ivOpenCamera.setOnClickListener {
            openCamera()
        }

        mBinding.ivOpenGallery.setOnClickListener {
            openGallery()
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
        openMedia(isCamera = true)
    }

    fun openGallery() {
        openMedia(isCamera = false)
    }


    private fun openMedia(isCamera: Boolean) {
        val permissions = if (isCamera) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        Dexter.withContext(this@MessagesActivity).withPermissions(*permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {

                            //Open camera if camera is true
                            if (isCamera) {
                                // Create a temp file to store the photo
                                photoFile = File.createTempFile("temp", ".jpg")
                                // Get the URI of the photo file
                                photoURI = FileProvider.getUriForFile(
                                    this@MessagesActivity,
                                    "weston.luke.messengerappmvvm.fileprovider",
                                    photoFile
                                )
                                //Todo future change this to activity result contract
                                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                startActivityForResult(takePictureIntent, CAMERA)
                            }
                            //Otherwise open the gallery
                            else {
                                //Todo future change this to activity result contract
                                val intent = Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                                startActivityForResult(intent, GALLERY)
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            })
            .onSameThread()
            .check()
    }


    //Todo future change this to activity result contract
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            try {
                // Get the image data from the file
                //TODO future simplify this image return, is it possible to return just a base64 string
                var imageData = FileInputStream(photoFile).use { it.readBytes() }
                //Get the correct orientation
                imageData = ImageUtils.rotateByteArrayIfRequired(imageData)
                //Change to base 64 string
                val imageDataAsBase64String = Base64.encodeToString(imageData, Base64.DEFAULT)
                //Save the image and return the full path
                val fullResImagePath = ImageUtils.saveImage(this, imageDataAsBase64String)
                //Send the image to the server
                mMessageViewModel.sendImage(
                    conversationId,
                    imageDataAsBase64String,
                    fullResImagePath,
                    this
                )
            } catch (e: Exception) {
                Log.e("Error displaying image from camera", e.message.toString())
                e.printStackTrace()
                toast("Error displaying image from camera")
            }

        } else if (requestCode == GALLERY) {
            try {
                data?.let {
                    val selectedPhotoUri = data.data
                    val imageAsString = ImageUtils.uriToBase64(selectedPhotoUri!!, this)
                    //Simple check for path from uri will work as we are just looking for an image
                    val imagePath = selectedPhotoUri.path
                    if (imageAsString != null && imagePath != null) {
                        mMessageViewModel.sendImage(
                            conversationId,
                            imageAsString,
                            imagePath,
                            this
                        )
                    } else {
                        throw NullPointerException("path is null or image is null")
                    }
                }
            } catch (e: Exception) {
                Log.e("Error loading image from gallery", e.message.toString())
                e.printStackTrace()
                toast("Error loading image from gallery")
            }
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
                    //Create an intent to go towards phone settings - specifically to application details settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    //use the package name of this app to be able to get to the specific application settings for this app
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    //Start the settings
                    startActivity(intent)
                }
                //Catch not found, print stack trace
                catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
    }

}