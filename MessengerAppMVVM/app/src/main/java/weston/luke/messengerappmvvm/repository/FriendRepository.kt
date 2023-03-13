package weston.luke.messengerappmvvm.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import weston.luke.messengerappmvvm.data.database.dao.FriendDao
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.Friend
import weston.luke.messengerappmvvm.data.database.entities.FriendshipStatus
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.NewFriendRequest
import weston.luke.messengerappmvvm.data.remote.request.UpdateFriendshipStatusRequest
import weston.luke.messengerappmvvm.data.remote.response.FriendRequestResponse

class FriendRepository(
    private val friendDao: FriendDao,
    private val conversationRepository: ConversationRepository,
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
                    friendStatus = FriendshipStatus.valueOf(it.friendshipStatus),
                    privateConversationId = it.conversationId
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
    ) {
        val conversationResponse = apiService.updateFriendshipStatus(
            UpdateFriendshipStatusRequest(
                selfUserId = selfUserId,
                friendUsername = friendUsername,
                friendshipStatus = friendshipStatus.toString()
            )
        )
        //If FriendshipStatus.Declined or friendshipStatus == FriendshipStatus.Removed- delete the friendship relationship
        //Conversation and messages are deleted in the server - will be automatically deleted when the user logins in again
        if (conversationResponse.success && friendshipStatus == FriendshipStatus.Declined || friendshipStatus == FriendshipStatus.Removed) {
            friendDao.deleteByFriendId(friendId)
        }
        //Else update friendship status
        else if (conversationResponse.success && friendshipStatus == FriendshipStatus.Friends) {
            friendDao.insertFriend(
                Friend(
                    friendId,
                    friendUsername,
                    friendshipStatus,
                    conversationResponse.id
                )
            )
            conversationRepository.insertConversation(
                Conversation(
                    conversationId = conversationResponse.id,
                    conversationName = conversationResponse.conversationName,
                    lastUpdatedDateTime = null
                )
            )
        }
    }


}