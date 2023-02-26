package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.databinding.FragmentConversationsBinding

class ConversationFragment: Fragment() {

    private var mBinding: FragmentConversationsBinding? = null
    private lateinit var conversationsAdapter: ConversationsAdapter
    private var conversationItems: List<Conversation> = listOf()
    private lateinit var conversationRecyclerView: RecyclerView

    private val mViewModel: ConversationAndFriendsViewModel by viewModels {
        ConversationAndFriendsViewModelFactory(
            (requireActivity().application as MessengerAppMVVMApplication).loggedInUserRepository,
            (requireActivity().application as MessengerAppMVVMApplication).conversationsRepository
        )
    }

    companion object {
        fun newInstance() = ConversationFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentConversationsBinding.inflate(inflater, container, false)



        //When conversations are updated the conversationItems are updated which updates the data in the recyclerview
        //then notify the adapter to update the view
        mViewModel.conversations.observe(viewLifecycleOwner) { conversations ->
            if(conversations != null){
                //Todo - change this so the recycler view isnt recreated everytime
                conversationsAdapter = ConversationsAdapter(conversationItems)
                conversationRecyclerView = mBinding!!.recyclerViewConversations
                conversationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                conversationRecyclerView.adapter = conversationsAdapter
            }

        }


        return mBinding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




    }

}