package weston.luke.messengerappmvvm.ui.conversationsAndFriends.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import weston.luke.messengerappmvvm.application.MessengerAppMVVMApplication
import weston.luke.messengerappmvvm.databinding.FragmentFriendsBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.FriendsViewModel
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels.FriendsViewModelFactory

class FriendFragment: Fragment() {

    private var mBinding: FragmentFriendsBinding? = null

    private val mViewModel: FriendsViewModel by viewModels {
        FriendsViewModelFactory(
            (requireActivity().application as MessengerAppMVVMApplication).conversationRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFriendsBinding.inflate(inflater, container, false)

        return mBinding!!.root
    }
}