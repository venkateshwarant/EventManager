package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.android.synthetic.main.layout_create_event_step1.*

import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.pojo.Event
import lu.uni.project.eventmanager.util.BundleKeys

class CreateEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        if (FirebaseAuth.getInstance().currentUser != null) {
            changeStatusBarColor(this)
            if (intent.extras != null) {
                eventName.setText(intent.extras!!.getString(BundleKeys.eventNameKey))
                eventDescription.setText(intent.extras!!.getString(BundleKeys.eventDescriptionKey))
            }
            close.setOnClickListener {
                var listener = DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
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
            next.setOnClickListener {
                if (eventName.text.toString() != "") {

                    var event = if (intent.extras != null) {
                        intent.extras
                    } else {
                        Bundle()
                    }
                    var intent = Intent(this, CreateEventStep2::class.java)
                    event?.putString(BundleKeys.eventNameKey, eventName.text.toString())
                    event?.putString(BundleKeys.eventDescriptionKey, eventDescription.text.toString())
                    intent.putExtras(event!!)
                    startActivity(intent)
                    this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                } else {
                    Toast.makeText(this, "Enter event name", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    fun setNextButtonColor() {
        if ((findViewById<View>(R.id.eventName) as EditText).text.toString()!="") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (findViewById<View>(R.id.next) as Button).background = applicationContext.getDrawable(R.drawable.shape_dark_button)
                (findViewById<View>(R.id.next) as Button).setTextColor(applicationContext.getColor(R.color.colorWhite))

            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (findViewById<View>(R.id.next) as Button).background = applicationContext.getDrawable(R.drawable.shape_rounded_corner)
                (findViewById<View>(R.id.next) as Button).setTextColor(applicationContext.getColor(R.color.colorBlack))

            }
        }
    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            setNextButtonColor()
        }
    }

    fun hideKeyBoard(view: View) {
        hideSoftKeyboard()
    }


    companion object {

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