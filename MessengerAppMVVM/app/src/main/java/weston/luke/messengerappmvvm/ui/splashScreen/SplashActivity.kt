package weston.luke.messengerappmvvm.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivitySplashBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationAndFriendsActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySplashBinding
    private val mSplashActivityViewModel: SplashActivityViewModel by viewModels {
        SplashActivityViewModelFactory(
            (application as MessengerAppMVVMApplication).loggedInUserRepository,
            (application as MessengerAppMVVMApplication).parentRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val context = this

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                //Check user is logged in after the fade in happens
                mSplashActivityViewModel.checkUserAlreadyLoggedIn(context)
            }
        })



        mSplashActivityViewModel.userAlreadyLoggedIn.observe(this){alreadyLoggedIn ->
            if(alreadyLoggedIn){
                val intent = Intent(this, ConversationAndFriendsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.nothing, R.anim.fade_out)
                finish()
            }
            else{
                startActivity(Intent(this@SplashActivity, ConversationAndFriendsActivity::class.java))
                overridePendingTransition(R.anim.nothing, R.anim.fade_out)
                finish()
            }


        }



        // Start the animation
        mBinding.splashLayout.startAnimation(fadeInAnimation)
    }
}