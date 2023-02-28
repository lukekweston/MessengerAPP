package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.databinding.ItemConversationBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.data.ConversationWithLatestMessage
import weston.luke.messengerappmvvm.util.utils

class ConversationsAdapter() : RecyclerView.Adapter<ConversationViewHolder>() {

    private var conversationsAndMessages: List<ConversationWithLatestMessage> = emptyList()
    private var onItemClickListener: onCardClickListener? = null

    fun setData(conversationsAndMessages: List<ConversationWithLatestMessage>) {
        this.conversationsAndMessages = conversationsAndMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val mBinding = ItemConversationBinding.inflate(inflater, parent, false)
        return ConversationViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversationsAndMessage = conversationsAndMessages[position]
        holder.bind(conversationsAndMessage)
        holder.itemView.setOnClickListener {
            onItemClickListener?.onCardClick(
                conversationsAndMessage.conversationId
            )
        }
    }

    override fun getItemCount() = conversationsAndMessages.size

    fun setOnItemClickListener(listener: onCardClickListener) {
        onItemClickListener = listener
    }

    interface onCardClickListener {
        fun onCardClick(conversationId: Int)
    }

}

class ConversationViewHolder(private val mBinding: ItemConversationBinding) :
    RecyclerView.ViewHolder(mBinding.root) {
    fun bind(conversationsAndMessage: ConversationWithLatestMessage) {
        mBinding.tvConversationTitle.text = conversationsAndMessage.conversationName
        mBinding.tvLastMessageBy.text = conversationsAndMessage.userName
        mBinding.tvLastMessageText.text = conversationsAndMessage.message
        mBinding.tvLastMessageTime.text = conversationsAndMessage.lastMessageTime?.format(utils.formatter) ?: ""
    }
}

