package weston.luke.messengerappmvvm.ui.conversationsAndFriends.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.FragmentConversationsBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.ConversationsAdapter
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.ConversationsViewModelFactory
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.ConversationsViewModel
import weston.luke.messengerappmvvm.util.hide
import weston.luke.messengerappmvvm.util.show

class ConversationFragment: Fragment(), ConversationsAdapter.onCardClickListener {

    private var mBinding: FragmentConversationsBinding? = null
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
        conversationRecyclerView = mBinding!!.recyclerViewConversations
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
        return mBinding!!.root
    }



    override fun onCardClick(conversationId: Int) {
        Toast.makeText(requireContext(), "go to conversation $conversationId", Toast.LENGTH_SHORT).show()
    }

}