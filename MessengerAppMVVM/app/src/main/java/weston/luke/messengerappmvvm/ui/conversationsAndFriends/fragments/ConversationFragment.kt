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

class ConversationFragment: Fragment(), ConversationsAdapter.onCardClickListener {

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
        conversationsAdapter = ConversationsAdapter()
        conversationRecyclerView = mBinding.recyclerViewConversations
        conversationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        conversationRecyclerView.adapter = conversationsAdapter

        conversationsAdapter.setOnItemClickListener(this)

        mViewModel.loadConversations()

        //When conversations/Messages are updated the conversationsWithLatestMessages are updated which updates the data in the recyclerview
        //then notify the adapter to update the view
        mViewModel.conversationsWithLatestMessages.observe(viewLifecycleOwner) { conversationsWithLatestMessages ->
            if (conversationsWithLatestMessages != null) {
                conversationsAdapter.setData(conversationsWithLatestMessages)
            }

        }
        return mBinding.root
    }



    override fun onCardClick(conversationId: Int) {
        val intent = Intent(requireActivity(), MessagesActivity::class.java)
        intent.putExtra(Constants.CONVERSATION_ID, conversationId)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}