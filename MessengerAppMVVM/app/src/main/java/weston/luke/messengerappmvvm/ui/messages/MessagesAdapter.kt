package weston.luke.messengerappmvvm.ui.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.databinding.ItemMessageRecievedBinding
import weston.luke.messengerappmvvm.databinding.ItemMessageSentBinding
import weston.luke.messengerappmvvm.util.Utils

class MessagesAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2


    private var messages: List<Message> = emptyList()
    private var loggedInUserId: Int = 0

    fun setData(loggedInUserId: Int, messages: List<Message>) {
        this.messages = messages
        this.loggedInUserId = loggedInUserId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SENT) {
            val mBinding = ItemMessageSentBinding.inflate(inflater, parent, false)
            SentMessageHolder(mBinding, context)
        } else {
            val mBinding = ItemMessageRecievedBinding.inflate(inflater, parent, false)
            ReceivedMessageHolder(mBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        var showDate = false
        //Shoe message if it is the first message in a conversation
        if (position == 0) {
            showDate = true
        }
        //Show date if the message is the first message for the day
        else if (message.timeSent.dayOfMonth != messages[position - 1].timeSent.dayOfMonth ||
            message.timeSent.month != messages[position - 1].timeSent.month) {
            showDate = true
        }
        if (holder.itemViewType == VIEW_TYPE_SENT) {
            (holder as SentMessageHolder).bind(message, showDate)
        } else {
            (holder as ReceivedMessageHolder).bind(message, showDate)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].userId == loggedInUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }


}


class ReceivedMessageHolder(private val mBinding: ItemMessageRecievedBinding) :
    RecyclerView.ViewHolder(mBinding.root) {
    fun bind(message: Message, showDate: Boolean) {

        val timestamp = message.timeUpdated?.format(Utils.formatDayMonthHourMin) ?: message.timeSent.format(
            Utils.formatHourMin
        )



        mBinding.textGchatDateOther.visibility = if (showDate) View.VISIBLE else View.GONE
        mBinding.textGchatDateOther.text = message.timeSent.format(Utils.formatDayMonth)

        mBinding.textGchatMessageOther.text = message.message
        mBinding.textGchatTimestampOther.text =
            if (message.timeUpdated != null) "Updated: $timestamp" else timestamp
        mBinding.textGchatUserOther.text = message.userName

        //todo show profile image
//        mBinding.imageGchatProfileOther
    }
}


class SentMessageHolder(private val mBinding: ItemMessageSentBinding, private val context: Context) :
    RecyclerView.ViewHolder(mBinding.root) {
    fun bind(message: Message, showDate: Boolean) {

        val timestamp = message.timeUpdated?.format(Utils.formatDayMonthHourMin) ?: message.timeSent.format(
            Utils.formatHourMin
        )



        mBinding.textGchatDateMe.visibility = if (showDate) View.VISIBLE else View.GONE
        mBinding.textGchatDateMe.text = message.timeSent.format(Utils.formatDayMonth)

        mBinding.textGchatMessageMe.text = message.message
        mBinding.textGchatTimestampMe.text =
            if (message.timeUpdated != null) "Updated: $timestamp" else timestamp


        //TODO update this so that messages that are only created display differently
//        if (message.status == SentStatus.CREATED) {
//            mBinding.textGchatDateMe.text = "Not sent"
//            mBinding.cardGchatMessageMe.setCardBackgroundColor(
//                ContextCompat.getColor(
//                    context,
//                    R.color.purple_200
//                )
//            )
//        }

    }
}
