package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.databinding.ItemConversationBinding

class ConversationsAdapter() : RecyclerView.Adapter<ConversationViewHolder>() {

    private var conversations: List<Conversation> = emptyList()

    fun setData(conversations: List<Conversation>) {
        this.conversations = conversations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val mBinding = ItemConversationBinding.inflate(inflater, parent, false)
        return ConversationViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
    }

    override fun getItemCount() = conversations.size

}

class ConversationViewHolder(private val mBinding: ItemConversationBinding) : RecyclerView.ViewHolder(mBinding.root){
    fun bind(conversation: Conversation){
        mBinding.tvConversationTitle.text = conversation.conversationName
        //Todo get other data and bind it
    }


}