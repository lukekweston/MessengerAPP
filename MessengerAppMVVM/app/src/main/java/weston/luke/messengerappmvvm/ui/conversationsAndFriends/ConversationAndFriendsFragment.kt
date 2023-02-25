package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import weston.luke.messengerappmvvm.R

class ConversationAndFriendsFragment : Fragment() {

    companion object {
        fun newInstance() = ConversationAndFriendsFragment()
    }

    private lateinit var viewModel: ConversationAndFriendsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConversationAndFriendsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}