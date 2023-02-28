package weston.luke.messengerappmvvm.ui.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivityMessagesBinding
import weston.luke.messengerappmvvm.ui.login.LoginViewModel
import weston.luke.messengerappmvvm.ui.login.LoginViewModelFactory
import weston.luke.messengerappmvvm.util.Constants
import weston.luke.messengerappmvvm.util.toast


//Got messaging UI from here
//https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui

class MessagesActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMessagesBinding
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
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        conversationId = intent.getIntExtra(Constants.CONVERSATION_ID, -1)
        if(conversationId == -1){
            this.toast("Error getting conversation")
        }

        lifecycleScope.launch {
            mMessageViewModel.getConversation(conversationId).collect { conversation ->
                supportActionBar!!.title = conversation.conversationName

            }
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