package weston.luke.messengerappmvvm.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    lateinit var mBinding: ActivityLoginBinding
    lateinit var mLoginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mLoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        //loadingSpinner.show()


       // if(mLoginViewModel.isLoggedIn){
            //Go to next screen - pass user
        //}


        //mBinding.btnLogin.setOnClickListener(mLoginViewModel.loginUser)





    }

}