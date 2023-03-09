package weston.luke.messengerappmvvm.ui.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.repository.MessageRepository

class FullSizeImageViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    var image = MutableLiveData<String>()

    fun getImage(messageId: Int){
        viewModelScope.launch {
            image.value = messageRepository.getFullResImage(messageId)
        }
    }


}

class FullSizeImageViewModelFactory(
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FullSizeImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FullSizeImageViewModel(
                messageRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

