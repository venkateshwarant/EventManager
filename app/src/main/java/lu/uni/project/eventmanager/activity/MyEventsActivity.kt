package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_event_step2.*
import kotlinx.android.synthetic.main.activity_my_events.*
import kotlinx.android.synthetic.main.activity_my_events.back
import kotlinx.android.synthetic.main.activity_saved_events.listView
import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.adapter.EventsAdapter
import lu.uni.project.eventmanager.pojo.Event

class MyEventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)
        var context= this
        act= this
        setupUI(myEventsContainer)
        changeStatusBarColor(this)
        val database = FirebaseDatabase.getInstance()
        val events = database.getReference("event")
        events.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eventList = mutableListOf<Event>()
                for (postSnapshot in dataSnapshot.children) {
                    val eventObj = postSnapshot.getValue(Event::class.java)
                    if(eventObj?.getUserId()?.contentEquals(FirebaseAuth.getInstance().currentUser?.uid!!)!!){
                        eventList.add(eventObj!!)
                    }
                }
                if (eventList.size > 0 && context!=null) {
                    listView.adapter= EventsAdapter(context, eventList,100,5)
                } else {

                }
                events.removeEventListener(this)

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        back.setOnClickListener{
            finish()
        }

    }
    companion object {
        var act:Activity?= null
        fun changeStatusBarColor(activity: Activity) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = activity.resources.getColor(R.color.colorWhite)
                val decorView = window.decorView
                var systemUiVisibilityFlags = decorView.systemUiVisibility
                systemUiVisibilityFlags = systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                decorView.systemUiVisibility = systemUiVisibilityFlags
            }
        }
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun hideSoftKeyboard(activity: Activity) {
            if ((activity != null) and (activity.currentFocus != null)) {
                val inputMethodManager = activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }

        fun setupUI(view: View) {
            if (view !is EditText) {
                view.setOnTouchListener { v, event ->
                    hideSoftKeyboard(act!!)
                    false
                }
            }
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val innerView = view.getChildAt(i)
                    setupUI(innerView)
                }
            }
        }
    }
}
