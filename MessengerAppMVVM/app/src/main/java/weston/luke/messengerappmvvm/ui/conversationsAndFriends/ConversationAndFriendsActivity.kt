package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.ActivityConversationAndFriendsBinding
import weston.luke.messengerappmvvm.ui.login.LoginActivity
import weston.luke.messengerappmvvm.util.toast

class ConversationAndFriendsActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityConversationAndFriendsBinding
    private lateinit var mNavController: NavController
    private val mConversationAndFriendsViewModel: ConversationAndFriendsViewModel by viewModels {
        ConversationAndFriendsViewModelFactory(
            (application as MessengerAppMVVMApplication).loggedInUserRepository,
            (application as MessengerAppMVVMApplication).conversationsRepository
        )
    }
    private val activityScope = CoroutineScope(lifecycleScope.coroutineContext + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityConversationAndFriendsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mNavController = findNavController(R.id.nav_host_conversation_and_friends)


//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_all_dishes,
//                R.id.navigation_favourite_dishes,
//                R.id.navigation_random_dish
//            )
//        )
//
//        setupActionBarWithNavController(mNavController, appBarConfiguration)
        setSupportActionBar(mBinding.toolbar)

        //Observe the logged in user, if its null return to login screen and clear the backstack
        mConversationAndFriendsViewModel.loggedInUser.observe(this) { loggedInUser ->
            if (loggedInUser == null) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, ConversationFragment.newInstance())
//                .commitNow()
//        }

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
                logoutUser()
            }
        }
        return true
    }


    private fun logoutUser() {
        activityScope.launch {
            mConversationAndFriendsViewModel.logoutUser()
        }
    }
}