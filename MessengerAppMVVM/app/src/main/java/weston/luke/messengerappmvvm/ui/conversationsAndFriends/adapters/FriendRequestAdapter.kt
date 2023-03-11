package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.data.database.entities.Friend
import weston.luke.messengerappmvvm.databinding.ItemFriendRequestBinding

class FriendRequestAdapter(private val onFriendRequestAcceptClick: (Int, String) -> Unit, private val onFriendRequestDeclineClick: (Int, String) -> Unit) :
    RecyclerView.Adapter<FriendRequestViewHolder>() {

    private var friendRequests: List<Friend> = emptyList()

    fun setData(friendRequest: List<Friend>) {
        this.friendRequests = friendRequest
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val mBinding = ItemFriendRequestBinding.inflate(inflater, parent, false)
        return FriendRequestViewHolder(mBinding, onFriendRequestAcceptClick, onFriendRequestDeclineClick)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        holder.bind(friendRequests[position])

    }

    override fun getItemCount() = friendRequests.size

}

class FriendRequestViewHolder(
    private val mBinding: ItemFriendRequestBinding,
    private val onFriendRequestAcceptClick: (Int, String) -> Unit,
    private val onFriendRequestDeclineClick: (Int, String) -> Unit,
) :
    RecyclerView.ViewHolder(mBinding.root) {
    fun bind(friend: Friend) {
        mBinding.tvFriendName.text = friend.friendUserName
        mBinding.buttonAccept.setOnClickListener { onFriendRequestAcceptClick(friend.friendId, friend.friendUserName) }
        mBinding.buttonDelete.setOnClickListener { onFriendRequestDeclineClick(friend.friendId, friend.friendUserName) }

    }
}

