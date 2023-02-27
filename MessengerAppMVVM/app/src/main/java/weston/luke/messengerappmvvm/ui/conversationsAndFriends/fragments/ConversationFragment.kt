package weston.luke.messengerappmvvm.ui.conversationsAndFriends.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.FragmentConversationsBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationsAdapter
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.ConversationsViewModelFactory
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.ConversationsViewModel

class ConversationFragment: Fragment() {

    private var mBinding: FragmentConversationsBinding? = null
    private lateinit var conversationsAdapter: ConversationsAdapter
    private lateinit var conversationRecyclerView: RecyclerView

    private val mViewModel: ConversationsViewModel by viewModels {
        ConversationsViewModelFactory(
            (requireActivity().application as MessengerAppMVVMApplication).conversationsRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentConversationsBinding.inflate(inflater, container, false)

        conversationsAdapter = ConversationsAdapter()
        conversationRecyclerView = mBinding!!.recyclerViewConversations
        conversationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        conversationRecyclerView.adapter = conversationsAdapter

        //When conversations are updated the conversationItems are updated which updates the data in the recyclerview
        //then notify the adapter to update the view
        mViewModel.conversations.observe(viewLifecycleOwner) { conversations ->
            if (conversations != null) {
                conversationsAdapter.setData(conversations)
            }

        }
        return mBinding!!.root
    }
}