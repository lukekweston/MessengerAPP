package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import weston.luke.messengerappmvvm.repository.ConversationsRepository
import java.lang.IllegalArgumentException

class FriendsViewModel(conversationsRepository: ConversationsRepository) : ViewModel(){

}

//Todo build this with the correct repository
class FriendsViewModelFactory( private val conversationsRepository: ConversationsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsViewModel(conversationsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}