package weston.luke.messengerappmvvm.ui.conversationsAndFriends.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.FragmentConversationsBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationsAdapter
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.ConversationsViewModel
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.ConversationsViewModelFactory
import weston.luke.messengerappmvvm.ui.messages.MessagesActivity
import weston.luke.messengerappmvvm.util.Constants

class ConversationFragment: Fragment(){

    private lateinit var mBinding: FragmentConversationsBinding
    private lateinit var conversationsAdapter: ConversationsAdapter
    private lateinit var conversationRecyclerView: RecyclerView


    private val mViewModel: ConversationsViewModel by viewModels {
        ConversationsViewModelFactory(
            (requireActivity().application as MessengerAppMVVMApplication).conversationRepository,
            (requireActivity().application as MessengerAppMVVMApplication).messageRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentConversationsBinding.inflate(inflater, container, false)

        conversationsAdapter = ConversationsAdapter(){ conversationId ->
            val intent = Intent(requireActivity(), MessagesActivity::class.java)
            intent.putExtra(Constants.CONVERSATION_ID, conversationId)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        conversationRecyclerView = mBinding.recyclerViewConversations
        conversationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        conversationRecyclerView.adapter = conversationsAdapter


        mViewModel.loadConversations()

        //Observe the latest messages and the conversations for changes
        mViewModel.latestMessagesAndConversations.observe(viewLifecycleOwner){ (latestMessages, conversations) ->
            conversationsAdapter.setData(latestMessages, conversations)
        }


        return mBinding.root
    }

}