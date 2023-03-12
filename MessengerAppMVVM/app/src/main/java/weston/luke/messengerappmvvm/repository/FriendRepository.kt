package weston.luke.messengerappmvvm.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import weston.luke.messengerappmvvm.data.database.dao.FriendDao
import weston.luke.messengerappmvvm.data.database.entities.Friend
import weston.luke.messengerappmvvm.data.database.entities.FriendshipStatus
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.NewFriendRequest
import weston.luke.messengerappmvvm.data.remote.request.UpdateFriendshipStatusRequest
import weston.luke.messengerappmvvm.data.remote.response.FriendRequestResponse
import weston.luke.messengerappmvvm.data.remote.response.SuccessResponse

class FriendRepository(
    private val friendDao: FriendDao,
    private val apiService: MessengerAPIService
) {

    private val friendRequests: LiveData<List<Friend>> =
        friendDao.getFriendsByFriendshipStatus(FriendshipStatus.Pending)
    private val friends: LiveData<List<Friend>> =
        friendDao.getFriendsByFriendshipStatus(FriendshipStatus.Friends)


    suspend fun sendFriendRequest(
        loggedInUser: Int,
        userNameOrEmail: String
    ): FriendRequestResponse {
        return apiService.sendFriendRequest(
            NewFriendRequest(
                selfUserId = loggedInUser,
                usernameOrEmail = userNameOrEmail
            )
        )
    }

    suspend fun insertFriend(friend: Friend) {
        friendDao.insertFriend(friend = friend)
    }

    fun getAllFriendRequests(): LiveData<List<Friend>> {
        return friendRequests
    }

    fun getAllFriends(): LiveData<List<Friend>> {
        return friends
    }

    @WorkerThread
    suspend fun insertFriends(friends: List<Friend>) = friendDao.insertFriends(friends)


    suspend fun getAllFriendsForUser(userId: Int) {
        val friends = apiService.getAllFriendsForUser(userId)
        friendDao.insertFriends(
            friends.map {
                Friend(
                    friendId = it.friendUserId,
                    friendUserName = it.friendUserName,
                    friendStatus = FriendshipStatus.valueOf(it.friendshipStatus)
                )
            }
        )

    }

    suspend fun deleteAllFriends() {
        friendDao.deleteAllFriends()
    }

    suspend fun updateFriendshipStatus(
        selfUserId: Int,
        friendId: Int,
        friendUsername: String,
        friendshipStatus: FriendshipStatus
    ): SuccessResponse {
        val response = apiService.updateFriendshipStatus(
            UpdateFriendshipStatusRequest(
                selfUserId = selfUserId,
                friendUsername = friendUsername,
                friendshipStatus = friendshipStatus.toString()
            )
        )
        //If FriendshipStatus.Declined - delete the friendship relationship
        if (response.success && friendshipStatus == FriendshipStatus.Declined) {
            friendDao.delete(Friend(friendId, friendUsername, friendshipStatus))
        }
        //Else update friendship status
        else if (response.success) {
            friendDao.insertFriend(Friend(friendId, friendUsername, friendshipStatus))
        }

        return response
    }


}