package weston.luke.messengerappmvvm.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    lateinit var mBinding: ActivityLoginBinding
    private val mLoginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as MessengerAppMVVMApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mLoginViewModel.loggedInUser.observe(this, Observer{ loggedInUser ->
            if(loggedInUser != null){
                //go to next screen
            }
        })

        //loadingSpinner.show()


        // if(mLoginViewModel.isLoggedIn){
        //Go to next screen - pass user
        //}


        mBinding.btnLogin.setOnClickListener {
            mLoginViewModel.loginUser()
           // Log.d("logged in user", mLoginViewModel.loggedInUser.value?.userName ?: "nada")
           // Log.d("logged in user", mLoginViewModel.loggedInUsers.value?.size.toString())
        }


    }

}