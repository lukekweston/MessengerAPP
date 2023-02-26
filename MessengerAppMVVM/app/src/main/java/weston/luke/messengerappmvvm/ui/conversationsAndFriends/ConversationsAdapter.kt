package weston.luke.messengerappmvvm.ui.conversationsAndFriends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.databinding.ItemConversationBinding

class ConversationsAdapter(private val conversations: List<Conversation>) : RecyclerView.Adapter<ConversationViewHolder>() {

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