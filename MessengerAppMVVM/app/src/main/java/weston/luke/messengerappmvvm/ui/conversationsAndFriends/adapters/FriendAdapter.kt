package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.data.database.entities.Friend
import weston.luke.messengerappmvvm.databinding.ItemFriendBinding

class FriendAdapter(
    private val onFriendMessageClick: (Int) -> Unit,
    private val onRemoveFriend: (Int, String) -> Unit
) :
    RecyclerView.Adapter<FriendViewHolder>() {

    private var friends: List<Friend> = emptyList()

    fun setData(friends: List<Friend>) {
        this.friends = friends
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val mBinding = ItemFriendBinding.inflate(inflater, parent, false)
        return FriendViewHolder(mBinding, onFriendMessageClick, onRemoveFriend)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friends[position])

    }

    override fun getItemCount() = friends.size

}

class FriendViewHolder(
    private val mBinding: ItemFriendBinding,
    private val onFriendMessageClick: (Int) -> Unit,
    private val onRemoveFriend: (Int, String) -> Unit
) :
    RecyclerView.ViewHolder(mBinding.root) {
    fun bind(friend: Friend) {
        mBinding.tvFriendName.text = friend.friendUserName
        mBinding.buttonMessage.setOnClickListener { onFriendMessageClick(friend.privateConversationId!!) }
        mBinding.cardView.setOnLongClickListener {
            onRemoveFriend(friend.friendId, friend.friendUserName)
            true
        }

    }
}

