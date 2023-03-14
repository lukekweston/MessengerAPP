package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.databinding.ActivityConversationAndFriendsBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.ConversationAndFriendsViewModel
import weston.luke.messengerappmvvm.ui.login.LoginActivity
import weston.luke.messengerappmvvm.util.Constants
import weston.luke.messengerappmvvm.util.toast

@AndroidEntryPoint
class ConversationAndFriendsActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityConversationAndFriendsBinding
    private lateinit var mNavController: NavController
    private val mConversationAndFriendsViewModel: ConversationAndFriendsViewModel by viewModels()
    private val activityScope = CoroutineScope(lifecycleScope.coroutineContext + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityConversationAndFriendsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)

        mNavController = findNavController(R.id.nav_host_conversation_and_friends)


        //If we are starting this activity from a friends notification - go to friends fragment
        val gotoFriendsFragment = intent.getBooleanExtra(Constants.GOTO_FRIEND_FRAGMENT, false)
        if(gotoFriendsFragment){
            mNavController.navigate(R.id.navigation_friends)
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_conversation,
                R.id.navigation_friends
            )
        )

        setupActionBarWithNavController(mNavController, appBarConfiguration)
        mBinding.navBottomNavigation.setupWithNavController(mNavController)


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("firebase", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and use the token as needed
            Log.d("firebase", "FCM registration token: $token")
        })


        //Observe the logged in user, if its null return to login screen and clear the backstack
        mConversationAndFriendsViewModel.loggedInUser.observe(this) { loggedInUser ->
            if (loggedInUser == null) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

    }


    //Add menu to the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_conversations, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                this.toast("Not implemented")
            }
            R.id.action_logout -> {
                logoutUser(this)
            }
        }
        return true
    }


    private fun logoutUser(context: Context) {
        activityScope.launch {
            mConversationAndFriendsViewModel.logoutUser(context)
        }
    }
}