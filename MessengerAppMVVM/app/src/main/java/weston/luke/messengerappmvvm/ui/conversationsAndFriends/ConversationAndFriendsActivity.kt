package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import weston.luke.messengerappmvvm.R

class ConversationAndFriendsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_and_friends)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ConversationAndFriendsFragment.newInstance())
                .commitNow()
        }
    }
}