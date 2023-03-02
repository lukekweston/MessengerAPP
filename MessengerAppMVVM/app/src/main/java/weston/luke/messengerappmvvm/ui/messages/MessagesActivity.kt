package weston.luke.messengerappmvvm.ui.messages

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivityMessagesBinding
import weston.luke.messengerappmvvm.util.Constants
import weston.luke.messengerappmvvm.util.toast


//Got messaging UI from here - modified a lot from this
//https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui

class MessagesActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMessagesBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private var firstLoad: Boolean = true

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
        if(conversationId == -1){
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
        mMessageViewModel.loggedInUserAndMessages.observe(this){(messages, loggedInUserId) ->
            messagesAdapter.setData(loggedInUserId = loggedInUserId, messages = messages)
            //On initial load scroll the recyclerview to the bottom when there are messages loaded
            if(firstLoad && messages.isNotEmpty()){
                messagesRecyclerView.scrollToPosition(messages.size - 1)
            }
        }

        mMessageViewModel.toastMessageToDisplay.observe(this) { toastMessageToDisplay ->
            if (toastMessageToDisplay != null) {
                toast(toastMessageToDisplay)
                //Todo, set up a way to resend the message
                //And make sure the message is displayed differently
            }
        }


        mBinding.buttonGchatSend.setOnClickListener{

            //close the keyboard
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mBinding.editGchatMessage.windowToken, 0)

            val textMessage = mBinding.editGchatMessage.text.toString()

            mMessageViewModel.sendMessage(textMessage, conversationId)

            //Clear the textMessage
            mBinding.editGchatMessage.text.clear()

            //Load data again to update recycler View
           // mMessageViewModel.loadData(conversationId)
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


}