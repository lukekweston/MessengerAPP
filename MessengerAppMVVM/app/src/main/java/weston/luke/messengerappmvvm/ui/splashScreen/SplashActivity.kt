package weston.luke.messengerappmvvm.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.databinding.ActivitySplashBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationAndFriendsActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, ConversationAndFriendsActivity::class.java))
                finish()
            }
        })


        //todo - create a view model
        //In the view model check if the server is running - if not go to a server down page - or stay here with a toast
        //In the view model check if the user is logged in - if so go to  conversations
        //If the user not logged in - go to login screen


        // Start the animation
        mBinding.splashLayout.startAnimation(fadeInAnimation)
    }
}