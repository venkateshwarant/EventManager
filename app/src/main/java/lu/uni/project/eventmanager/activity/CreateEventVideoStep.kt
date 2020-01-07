package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_event_step3.*
import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.adapter.SliderAdapter
import lu.uni.project.eventmanager.util.BundleKeys
import lu.uni.project.eventmanager.util.GlobalUtil
import java.io.IOException


class CreateEventVideoStep : AppCompatActivity() {
    private val REQUEST_CODE=123
    var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event_video_step)
        findViewById<View>(R.id.uploadVideoButton).setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, REQUEST_CODE)
        }
        changeStatusBarColor(this)
        next.setOnClickListener{
            var event= intent.extras
            event?.putString(BundleKeys.videoListKey, videoList.toString())
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
    open fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = managedQuery(uri, projection, null, null, null)
        return if (cursor != null) {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === Activity.RESULT_OK) {
            if (requestCode === REQUEST_CODE) {
                var selectedVideoPath = getPath(data?.getData())
                try {
                    if (selectedVideoPath == null) {
                        Log.e("","selected video path = null!")
                    } else {
                        var uriList= mutableListOf<Uri>()
                        uriList.add(Uri.parse(selectedVideoPath))
                        GlobalUtil.videoList= uriList
                        val adapterinner = SliderAdapter(this, uriList)
                        val mVideoView = findViewById(R.id.videoView) as VideoView
                        mVideoView.visibility=View.VISIBLE
                        if (mVideoView != null) {
                            mVideoView.setVideoURI(Uri.parse(selectedVideoPath))
                            mVideoView.requestFocus()
                            mVideoView.start()
                        }
                        videoView.setOnPreparedListener(object : OnPreparedListener {
                            override fun onPrepared(med: MediaPlayer) {
                                mp=med
                            }
                        })
                        videoView.setOnClickListener{
                            if(videoView.isPlaying){
                                videoView.pause()
                            }else{
                                videoView.start()
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }
        }
    }
    companion object {
        var videoList: List<Uri>?= null
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
