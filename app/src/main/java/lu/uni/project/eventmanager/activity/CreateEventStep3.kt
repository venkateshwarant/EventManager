package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_create_event_step3.*
import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.adapter.SliderAdapter
import lu.uni.project.eventmanager.pojo.Event
import lu.uni.project.eventmanager.util.BundleKeys
import lu.uni.project.eventmanager.util.GlobalUtil
import java.io.File



class CreateEventStep3 : AppCompatActivity(),  BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate {
//    internal var sliderView: SliderView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event_step3)
        findViewById<View>(R.id.uploadImageButton).setOnClickListener {
            val pickerDialog = BSImagePicker.Builder("lu.uni.project.eventmanager.fileprovider")
                    .setMaximumDisplayingImages(Integer.MAX_VALUE)
                    .isMultiSelect
                    .setMinimumMultiSelectCount(3)
                    .setMaximumMultiSelectCount(6)
                    .build()
            pickerDialog.show(supportFragmentManager, "picker")
        }
        changeStatusBarColor(this)
        next.setOnClickListener{
            var event= intent.extras
            event?.putString(BundleKeys.imagesListKey, imagesList.toString())
            var intent= Intent(this, CreateEventStep4::class.java)
            intent.putExtras( event!!)
            startActivity(intent)
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
        }
        back.setOnClickListener{
            finish()
        }
        close.setOnClickListener{
            var listener= DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                val gotoScreenVar = Intent(this, BottomNavigationActivity::class.java)
                gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(gotoScreenVar)
                this.overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right)
            }
            AlertDialog.Builder(this)
                    .setTitle("Discard event?")
                    .setMessage("Are you sure you want to discard this event?")
                    .setPositiveButton(android.R.string.yes, listener)
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }
    }

    override fun loadImage(imageFile: File, ivImage: ImageView) {
        Glide.with(this).load(imageFile).into(ivImage)
    }

    override fun onMultiImageSelected(uriList: List<Uri>, tag: String?) {
        imagesList= uriList
        GlobalUtil.imagesList= uriList
        val adapterinner = SliderAdapter(this, uriList)

        imageSlider?.visibility= View.VISIBLE
        imageSlider?.sliderAdapter = adapterinner

        imageSlider?.setIndicatorAnimation(IndicatorAnimations.SLIDE) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider?.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION)
//        sliderView?.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        imageSlider?.indicatorSelectedColor = Color.WHITE
        imageSlider?.indicatorUnselectedColor = Color.GRAY
//        sliderView?.startAutoCycle()

        imageSlider?.setOnIndicatorClickListener { position -> imageSlider!!.currentPagePosition = position }

    }

    override fun onSingleImageSelected(uri: Uri, tag: String) {

    }
    companion object {
        var imagesList: List<Uri>?= null

        fun changeStatusBarColor(activity: Activity) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = activity.resources.getColor(R.color.colorButtonBlack)
                val decorView = window.decorView
                var systemUiVisibilityFlags = decorView.systemUiVisibility
                systemUiVisibilityFlags = systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                decorView.systemUiVisibility = systemUiVisibilityFlags
            }
        }
    }

}
