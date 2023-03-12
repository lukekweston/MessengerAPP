package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.databinding.ItemConversationBinding
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.data.ConversationWithLatestMessage
import weston.luke.messengerappmvvm.util.Utils

class ConversationsAdapter(private val onConversationClick: (Int) -> Unit) :
    RecyclerView.Adapter<ConversationViewHolder>() {

    private var conversationsAndMessages: List<ConversationWithLatestMessage> = emptyList()


    //Only show conversations that have messages - dont show a conversation that was created
    //By a user but no messages were sent
    fun setData(latestMessages: List<LatestMessage?>, conversations: List<Conversation>) {
        val conversationsWithLatestMessages: MutableList<ConversationWithLatestMessage> =
            mutableListOf()

        for (conversation in conversations) {

            val latestMessage: LatestMessage? =
                latestMessages.firstOrNull { it?.conversationId == conversation.conversationId }

            //Only show the conversations that have a message
            if (latestMessage != null) {

                //Get the lastestMessageMessage - if it was a text message, display the text.
                //If it was a photo image display "Sent a photo"
                val latestMessageMessage =
                    if (!latestMessage.message.isNullOrEmpty()) latestMessage.message else (if (!latestMessage.pathToSavedLowRes.isNullOrEmpty()) "Sent a photo" else "")

                conversationsWithLatestMessages += ConversationWithLatestMessage(
                    conversationId = conversation.conversationId,
                    conversationName = conversation.conversationName ?: "Unnamed Conversation",
                    userName = latestMessage.userName ?: "",
                    //display the message as the last message, or "Sent a photo" if it was a photo
                    message = latestMessageMessage,
                    lastMessageTime = latestMessage.latestTime
                )
            }
        }


        this.conversationsAndMessages =
            conversationsWithLatestMessages.sortedByDescending { it.lastMessageTime }
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
            onConversationClick(conversationsAndMessage.conversationId)
        }
    }

    override fun getItemCount() = conversationsAndMessages.size

}

class ConversationViewHolder(private val mBinding: ItemConversationBinding) :
    RecyclerView.ViewHolder(mBinding.root) {
    fun bind(conversationsAndMessage: ConversationWithLatestMessage) {
        mBinding.tvConversationTitle.text = conversationsAndMessage.conversationName
        mBinding.tvLastMessageBy.text = conversationsAndMessage.userName
        mBinding.tvLastMessageText.text = conversationsAndMessage.message
        mBinding.tvLastMessageTime.text =
            conversationsAndMessage.lastMessageTime?.format(Utils.formatDayMonthHourMin) ?: ""
    }
}

