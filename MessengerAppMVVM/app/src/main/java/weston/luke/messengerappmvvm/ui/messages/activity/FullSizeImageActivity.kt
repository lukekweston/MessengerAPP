package weston.luke.messengerappmvvm.ui.messages.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import weston.luke.messengerappmvvm.R
import weston.luke.messengerappmvvm.databinding.ActivityFullSizeImageBinding
import weston.luke.messengerappmvvm.ui.messages.viewModels.FullSizeImageViewModel
import weston.luke.messengerappmvvm.util.Constants.IMAGE_BY_USERNAME
import weston.luke.messengerappmvvm.util.Constants.IMAGE_FILE_NAME
import weston.luke.messengerappmvvm.util.Constants.IMAGE_ID
import weston.luke.messengerappmvvm.util.ImageUtils
import weston.luke.messengerappmvvm.util.hide
import weston.luke.messengerappmvvm.util.show
import weston.luke.messengerappmvvm.util.toast


@AndroidEntryPoint
class FullSizeImageActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityFullSizeImageBinding

    private val mFullSizeImageViewModel: FullSizeImageViewModel by viewModels()

    private lateinit var imageBase64String : String
    private lateinit var imageFileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFullSizeImageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.tvImageFromUser.text = intent.getStringExtra(IMAGE_BY_USERNAME)

        val imageId = intent.getIntExtra(IMAGE_ID,-1)
        imageFileName = intent.getStringExtra(IMAGE_FILE_NAME).toString()
        if(imageId == -1){
            toast("Error getting message")
            onBackPressed()
        }

        //get the image
        mFullSizeImageViewModel.getImage(imageId)

        mFullSizeImageViewModel.image.observe(this){imageString ->
            if(imageString != null){
                imageBase64String = imageString

                val decodedString = Base64.decode(imageString, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)


                Glide.with(this)
                    .load(bitmap)
                    .into(mBinding.photoView)

                mBinding.loadingSpinner.hide()
                mBinding.tvSaveImage.show()

                mBinding.ivArrowBack.bringToFront()
                mBinding.tvSaveImage.bringToFront()
                mBinding.tvImageFromUser.bringToFront()


            }
        }

        mBinding.ivArrowBack.setOnClickListener {
            onBackPressed()
        }


        mBinding.tvSaveImage.setOnClickListener {
            //Save image to internal storage
            ImageUtils.saveImage(this, imageBase64String, overrideFileName = imageFileName)
            //Display a toast
            toast("Saved Image")

            //When success set text to saved
            mBinding.tvSaveImage.text = "Saved"
            mBinding.tvSaveImage.isEnabled = false
            mBinding.tvSaveImage.isClickable = false
        }
    }

    //Animate the transition back nicely
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}