package weston.luke.messengerappmvvm.ui.messages.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.repository.MessageRepository
import javax.inject.Inject

@HiltViewModel
class FullSizeImageViewModel
    @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    var image = MutableLiveData<String>()

    fun getImage(messageId: Int){
        viewModelScope.launch {
            image.value = messageRepository.getFullResImage(messageId)
        }
    }
}

