package weston.luke.messengerappmvvm.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivityLoginBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationAndFriendsActivity
import weston.luke.messengerappmvvm.util.hide
import weston.luke.messengerappmvvm.util.show

class LoginActivity : AppCompatActivity() {


    lateinit var mBinding: ActivityLoginBinding
    private val mLoginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as MessengerAppMVVMApplication).repository)
    }
    private val activityScope = CoroutineScope(lifecycleScope.coroutineContext + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //Check for logged in user
        checkForAlreadyLoggedInUser()


        mLoginViewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        mLoginViewModel.invalidUserNameOrPassword.observe(this) { invalidUserNameOrPassword ->
            if (invalidUserNameOrPassword) {
                mBinding.tilPassword.error = getString(R.string.invalid_username_or_password)
                mBinding.etPassword.setText("")
            } else {
                mBinding.tilPassword.error = null
            }
        }

        mLoginViewModel.loggingUserIn.observe(this) { loggingUserIn ->
            if (loggingUserIn) {
                mBinding.content.hide()
                mBinding.loadingSpinner.show()
            } else {
                mBinding.content.show()
                mBinding.loadingSpinner.hide()
            }

        }
        

        mBinding.btnLogin.setOnClickListener {
            mBinding.btnLogin.isEnabled = false
            val successfulLogin = mLoginViewModel.loginUser(
                userName = mBinding.etUsername.text.toString(),
                password = mBinding.etPassword.text.toString()
            )
            if (successfulLogin) {
                //Goto next screen
                //endLoginScreenAndGoToConversations()
            }
//          Else stay here - error login message will work with live data


            mBinding.btnLogin.isEnabled = true
        }
    }

    //Check user is already logged in, if they are, skip to the next screen
    //Todo, go to last opened screen?

    fun checkForAlreadyLoggedInUser(){
        activityScope.launch {
            if (mLoginViewModel.checkUserAlreadyLoggedIn()) {
                endLoginScreenAndGoToConversations()
            } else {
                mBinding.loadingSpinner.hide()
                mBinding.content.show()
            }
        }
    }

    fun endLoginScreenAndGoToConversations() {
        val intent = Intent(this, ConversationAndFriendsActivity::class.java)
        startActivity(intent)
        //Close this activity, should only be able to get back here if you log out
        finish()
    }



}