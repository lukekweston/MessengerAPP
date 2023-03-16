package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.Friend
import weston.luke.messengerappmvvm.data.database.entities.FriendshipStatus
import weston.luke.messengerappmvvm.data.remote.response.FriendRequestResponse
import weston.luke.messengerappmvvm.repository.FriendRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel
    @Inject constructor(
    private val friendRepository: FriendRepository,
    loggedInUserRepository: LoggedInUserRepository
) : ViewModel() {


    val loggedInUser = loggedInUserRepository.loggedInUser

    val friendRequests = friendRepository.getAllFriendRequests()
    val friends = friendRepository.getAllFriends()


    private val _friendRequestResponse = MutableLiveData<FriendRequestResponse>()

    val friendRequestResponse: LiveData<FriendRequestResponse>
        get() = _friendRequestResponse


    fun sendFriendRequest(usernameOrEmail: String): FriendRequestResponse? {
        var friendRequestResponse: FriendRequestResponse? = null
        viewModelScope.launch {

            friendRequestResponse =
                friendRepository.sendFriendRequest(loggedInUser.value!!.userId, usernameOrEmail)

            if (friendRequestResponse!!.friendRequestSent) {
                friendRepository.insertFriend(
                    Friend(
                        friendId = friendRequestResponse!!.friendUserId!!,
                        friendUserName = friendRequestResponse!!.friendUserName!!,
                        friendStatus = FriendshipStatus.Sent,
                        privateConversationId = null
                    )
                )
            }
            _friendRequestResponse.value = friendRequestResponse!!
        }

        return friendRequestResponse
    }

    fun acceptFriendRequest(friendId: Int, friendUsername: String){
        viewModelScope.launch {
            friendRepository.updateFriendshipStatus(
                loggedInUser.value!!.userId,
                friendId,
                friendUsername,
                FriendshipStatus.Friends
            )
        }
    }


    fun declineFriendRequest(friendId: Int, friendUsername: String) {
        viewModelScope.launch {
            friendRepository.updateFriendshipStatus(
                loggedInUser.value!!.userId,
                friendId,
                friendUsername,
                FriendshipStatus.Declined
            )
        }
    }

    fun removeFriend(friendId: Int, friendUsername: String) {
        viewModelScope.launch {
            friendRepository.updateFriendshipStatus(
                loggedInUser.value!!.userId,
                friendId,
                friendUsername,
                FriendshipStatus.Removed
            )
        }
    }
}
