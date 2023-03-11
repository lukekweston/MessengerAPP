package weston.luke.messengerappmvvm.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivityLoginBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationAndFriendsActivity
import weston.luke.messengerappmvvm.util.hide
import weston.luke.messengerappmvvm.util.show

class LoginActivity : AppCompatActivity() {


    lateinit var mBinding: ActivityLoginBinding
    private val mLoginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            (application as MessengerAppMVVMApplication).loggedInUserRepository,
            (application as MessengerAppMVVMApplication).parentRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //Check for logged in user
        mLoginViewModel.checkUserAlreadyLoggedIn(this)

        mBinding.loadingSpinner.hide()
        mBinding.content.show()


        //When the user has logged in successfully go to the next screen
        mLoginViewModel.successfullyCheckedUserIsLoggedIn.observe(this) { success ->
            if (success == true) {
                mBinding.loadingSpinner.show()
                mBinding.content.hide()
                endLoginScreenAndGoToConversations()
            }
        }

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
            mBinding.btnLogin.isClickable = false
            //close the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mBinding.etPassword.windowToken, 0)
            inputMethodManager.hideSoftInputFromWindow(mBinding.etUsername.windowToken, 0)

            mLoginViewModel.loginUser(
                userName = mBinding.etUsername.text.toString(),
                password = mBinding.etPassword.text.toString(),
                this
            )
            mBinding.btnLogin.isEnabled = true
            mBinding.btnLogin.isClickable = true
        }
    }

    private fun endLoginScreenAndGoToConversations() {
        val intent = Intent(this, ConversationAndFriendsActivity::class.java)
        startActivity(intent)
        //Close this activity, should only be able to get back here if you log out
        finish()
    }


}