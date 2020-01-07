package lu.uni.project.eventmanager.bottomsheet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import lu.uni.project.eventmanager.R
import com.wisnu.datetimerangepickerandroid.CalendarPickerView
import java.util.*


class FillterBottomSheetFragment(private val onFilterSelectListener: OnFilterSelectListener): SuperBottomSheetFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        var root= inflater.inflate(R.layout.layout_filters, container, false)
        var dateRangeFilter= root.findViewById<LinearLayout>(R.id.dateRangeFilter)
        var distanceFilter= root.findViewById<LinearLayout>(R.id.distanceFilter)
        var distanceHolder= root.findViewById<LinearLayout>(R.id.distanceHolder)
        var distanceText= root.findViewById<TextView>(R.id.seekBarDistance)
        var dateRangeHolder= root.findViewById<LinearLayout>(R.id.dateRangeHolder)
        var distanceSeeker= root.findViewById<SeekBar>(R.id.distanceSeeker)
        var filterHolder= root.findViewById<LinearLayout>(R.id.filterListContent)
        var filterSelect= root.findViewById<LinearLayout>(R.id.filterSelect)
        var filterTitle= root.findViewById<TextView>(R.id.filterTitle)
        filterTitle.text = "Filters"
        dateRangeFilter.setOnClickListener{
            filterTitle.text = "Date range"
            filterSelect.visibility=View.VISIBLE
            dateRangeHolder.animation=AnimationUtils.loadAnimation(context,R.anim.anim_slide_in_left)
            filterHolder.animation=AnimationUtils.loadAnimation(context,R.anim.anim_slide_out_left)
            filterHolder.visibility=View.GONE
            dateRangeHolder.visibility=View.VISIBLE
            val nextYear = Calendar.getInstance()
            nextYear.add(Calendar.YEAR, 1)
            val calendar = root.findViewById(R.id.calendar_view) as CalendarPickerView
            val today = Date()
            calendar.init(today, nextYear.getTime())
                    .withSelectedDate(today).inMode(CalendarPickerView.SelectionMode.RANGE)
            filterSelect.setOnClickListener{
                var selectedDates = calendar.selectedDates
                if(selectedDates!=null && selectedDates.size>1){
                    filterSelect.visibility=View.GONE
                    dismiss()
                    onFilterSelectListener.onSelectDateRange(selectedDates[0], selectedDates[selectedDates.size-1])
                }else{
                    Toast.makeText(context,"Select date range!",Toast.LENGTH_LONG)
                }
            }

        }
        distanceFilter.setOnClickListener{
            filterTitle.text = "Distance range"
            filterSelect.visibility=View.VISIBLE
            distanceHolder.animation=AnimationUtils.loadAnimation(context,R.anim.anim_slide_in_left)
            filterHolder.animation=AnimationUtils.loadAnimation(context,R.anim.anim_slide_out_left)
            filterHolder.visibility=View.GONE
            distanceHolder.visibility=View.VISIBLE
            distanceSeeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    distanceText.text = "$i KM"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
            filterSelect.setOnClickListener{
                var range: Int=distanceSeeker.progress
                if(range>1){
                    filterSelect.visibility=View.GONE
                    dismiss()
                    onFilterSelectListener.onSelectDistanceRange(range)
                }else{
                    Toast.makeText(context,"Select distance range!",Toast.LENGTH_LONG)
                }
            }
        }
        return root
    }

    override fun getCornerRadius() = context!!.resources.getDimension(R.dimen.btm_sht_crnr_radius)

    override fun getStatusBarColor() = Color.BLACK

    interface OnFilterSelectListener  {
        fun onSelectDateRange(from:Date, to:Date)
        fun onSelectDistanceRange(range:Int)
    }
}