package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.takisoft.datetimepicker.DatePickerDialog
import com.takisoft.datetimepicker.TimePickerDialog
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import kotlinx.android.synthetic.main.activity_create_event_step3.back
import kotlinx.android.synthetic.main.activity_create_event_step3.close
import kotlinx.android.synthetic.main.activity_create_event_step3.next
import kotlinx.android.synthetic.main.activity_create_event_step4.*
import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.adapter.SimpleArrayListAdapter
import lu.uni.project.eventmanager.util.BundleKeys
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateEventStep4 : AppCompatActivity() {
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
        setContentView(R.layout.activity_create_event_step4)
        changeStatusBarColor(this)
        next.setOnClickListener{
            var event= intent.extras?.getBundle(BundleKeys.event)
            event?.putString(BundleKeys.startDateKey, startDateText.text.toString())
            event?.putString(BundleKeys.endDateKey, endDateText.text.toString())
            event?.putString(BundleKeys.startTimeKey, startTimeText.text.toString())
            event?.putString(BundleKeys.endTimeKey, endTimeText.text.toString())

            var intent= Intent(this, CreateEventStep5::class.java)
            intent.putExtra(BundleKeys.event, event)
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
        initListValue()
        val cal = Calendar.getInstance()
        startDate.setOnClickListener{
            val dpd = DatePickerDialog(this, { view1, year, month, dayOfMonth ->
                startDateText.text= String.format("%d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
            dpd.show()
        }
        endDate.setOnClickListener{
            val dpd = DatePickerDialog(this, { view1, year, month, dayOfMonth ->
                endDateText.text= String.format("%d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
            dpd.show()
        }
        startTime.setOnClickListener{
            val tpd = TimePickerDialog(this, { view1, hourOfDay, minute ->
                startTimeText.text= String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(this))
            tpd.show()
        }
        endTime.setOnClickListener{
            val tpd = TimePickerDialog(this, { view1, hourOfDay, minute ->
                endTimeText.text= String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(this))
            tpd.show()
        }
        mSimpleArrayListAdapter = SimpleArrayListAdapter(this, mStrings)
        mSearchableSpinner = findViewById(R.id.SearchableSpinner) as SearchableSpinner
        mSearchableSpinner?.setAdapter(mSimpleArrayListAdapter)
        mSearchableSpinner?.setOnItemSelectedListener(mOnItemSelectedListener)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault())
        val currentLocalTime = calendar.time
        val date = SimpleDateFormat("z", Locale.getDefault())
        val localTime = date.format(currentLocalTime)
        for(i in 0 until mStrings.size){
            if(localTime== mStrings[i]){
                mSearchableSpinner?.setSelectedItem(i+1)
                break
            }
        }
        mSearchableSpinner?.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {

            }

            override fun spinnerIsClosing() {

            }
        })

    }
    fun initListValue(){
        mStrings.clear()
        var ids= TimeZone.getAvailableIDs()
        for(i in ids.indices){
            if(TimeZone.getTimeZone(ids[i]).displayName.contains("GMT")){
                mStrings.add(TimeZone.getTimeZone(ids[i]).displayName)
            }
        }
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
