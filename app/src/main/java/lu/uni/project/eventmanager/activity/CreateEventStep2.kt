package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import kotlinx.android.synthetic.main.activity_create_event.next
import kotlinx.android.synthetic.main.activity_create_event_step2.*
import kotlinx.android.synthetic.main.layout_create_event_step1.*
import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.adapter.SimpleArrayListAdapter
import lu.uni.project.eventmanager.pojo.Event
import lu.uni.project.eventmanager.util.BundleKeys
import java.util.ArrayList

class CreateEventStep2 : AppCompatActivity() {
    private var mSearchableSpinner: SearchableSpinner? = null
    private var mSimpleArrayListAdapter: SimpleArrayListAdapter? = null
    private val mStrings = ArrayList<String>()
    private val mOnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(view: View, position: Int, id: Long) {
//            Toast.makeText(this@MainActivity, "Item on position " + position + " : " + mSimpleListAdapter.getItem(position) + " Selected", Toast.LENGTH_SHORT).show()
        }

        override fun onNothingSelected() {
//            Toast.makeText(this@MainActivity, "Nothing Selected", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event_step2)
        initListValues()
        changeStatusBarColor(this)
        mSimpleArrayListAdapter = SimpleArrayListAdapter(this, mStrings)
        mSearchableSpinner = findViewById(R.id.SearchableSpinner) as SearchableSpinner
        mSearchableSpinner?.setAdapter(mSimpleArrayListAdapter)
        mSearchableSpinner?.setOnItemSelectedListener(mOnItemSelectedListener)
        mSearchableSpinner?.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {

            }

            override fun spinnerIsClosing() {

            }
        })

        findViewById<LinearLayout>(R.id.close).setOnClickListener{
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
        next.setOnClickListener{
            var event= intent.extras
            event?.putString(BundleKeys.eventCategoryKey, mSearchableSpinner?.selectedItem?.toString())
            var intent= Intent(this, CreateEventStep3::class.java)
            intent.putExtras( event!!)
            startActivity(intent)
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
        }
        back.setOnClickListener{
            finish()
        }
        CreateEventStep3.changeStatusBarColor(this)
    }

    private fun initListValues() {
        mStrings.add("Birthday parties")
        mStrings.add("Wedding")
        mStrings.add("Wedding Reception")
        mStrings.add("Friends get together")
        mStrings.add("Festival gatherings")
        mStrings.add("Music Show")
        mStrings.add("Visual Arts")
        mStrings.add("Fashion")
        mStrings.add("Film")
        mStrings.add("Nightlife")
        mStrings.add("Seminars")
        mStrings.add("Conferences")
        mStrings.add("Product Launch")
        mStrings.add("Networking Events")
        mStrings.add("Meetings")
        mStrings.add("Business Dinners")
        mStrings.add("Sports Events")
        mStrings.add("Sponsored Runs")
        mStrings.add("Sponsored Cycling")
        mStrings.add("Sponsored Skydiving")
        mStrings.add("Sponsored Walks")
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mSearchableSpinner!!.isInsideSearchEditText(event)) {
            mSearchableSpinner?.hideEdit()
        }
        return super.onTouchEvent(event)
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
