package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_saved_events.*
import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.adapter.EventsAdapter
import lu.uni.project.eventmanager.pojo.Event

class SavedEventsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_events)
        var context= this
        changeStatusBarColor(this)
        val database = FirebaseDatabase.getInstance()
        val savedEventsRef = database.getReference("saved").child(FirebaseAuth.getInstance().uid!!)
        savedEventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outputList = mutableListOf<String>()
                for (postSnapshot in dataSnapshot.children) {
                    val event = postSnapshot.key
                    outputList.add(event!!)
                }
                if (outputList.size > 0 && context!=null) {
                    val savedEventsRef = database.getReference("event")
                    savedEventsRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val eventList = mutableListOf<Event>()
                            for (postSnapshot in dataSnapshot.children) {
                                val eventObj = postSnapshot.getValue(Event::class.java)
                                for (ev in outputList) {
                                    if(ev.contentEquals(eventObj?.eventId.toString())){
                                        eventList.add(eventObj!!)
                                    }
                                }
                            }
                            if (eventList.size > 0 && context!=null) {
                                listView.adapter= EventsAdapter(context, eventList,100,5)
                            } else {

                            }
                            savedEventsRef.removeEventListener(this)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                } else {

                }
                savedEventsRef.removeEventListener(this)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    companion object {

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
    }
}
