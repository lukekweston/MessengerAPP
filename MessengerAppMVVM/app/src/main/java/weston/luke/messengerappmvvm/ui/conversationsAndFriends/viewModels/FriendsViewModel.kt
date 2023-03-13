package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels


import androidx.lifecycle.*
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.Friend
import weston.luke.messengerappmvvm.data.database.entities.FriendshipStatus
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.response.FriendRequestResponse
import weston.luke.messengerappmvvm.repository.FriendRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository

class FriendsViewModel(
    private val friendRepository: FriendRepository,
    private val loggedInUserRepository: LoggedInUserRepository
) : ViewModel() {


    val loggedInUser = MutableLiveData<LoggedInUser>()

    val friendRequests = friendRepository.getAllFriendRequests()
    val friends = friendRepository.getAllFriends()


    private val _friendRequestResponse = MutableLiveData<FriendRequestResponse>()

    val friendRequestResponse: LiveData<FriendRequestResponse>
        get() = _friendRequestResponse

    fun loadData() {
        viewModelScope.launch {
            loggedInUserRepository.loggedInUser.collect {
                loggedInUser.value = it
            }
        }
    }

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

//Todo build this with the correct repository
class FriendsViewModelFactory(
    private val friendsRepository: FriendRepository,
    private val loggedInUserRepository: LoggedInUserRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsViewModel(friendsRepository, loggedInUserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}