package lu.uni.project.eventmanager.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*

import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.tiper.MaterialSpinner
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import kotlinx.android.synthetic.main.layout_create_event_step1.*

import java.io.File

import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.adapter.SimpleArrayListAdapter
import java.util.ArrayList

class CreateEventActivity : AppCompatActivity(), BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate {

    private val RESULT_LOAD_IMAGE = 11
    private var img1: ImageView? = null
    private var img2: ImageView? = null
    private var img3: ImageView? = null
    private var img4: ImageView? = null
    private var img5: ImageView? = null
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
        setContentView(R.layout.activity_create_event)
        changeStatusBarColor(this)
        initListValues()
        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)
        img4 = findViewById(R.id.img4)
        img5 = findViewById(R.id.img5)
        /*findViewById<View>(R.id.uploadImageButton).setOnClickListener {
            val pickerDialog = BSImagePicker.Builder("lu.uni.project.eventmanager.fileprovider")
                    .setMaximumDisplayingImages(Integer.MAX_VALUE)
                    .isMultiSelect
                    //                        .setMinimumMultiSelectCount(3)
                    //                        .setMaximumMultiSelectCount(6)
                    .build()
            pickerDialog.show(supportFragmentManager, "pick events image")
        }*/
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
    private fun initListValues() {
        mStrings.add("Brigida Kurz")
        mStrings.add("Tracy Mckim")
        mStrings.add("Iesha Davids")
        mStrings.add("Ozella Provenza")
        mStrings.add("Florentina Carriere")
        mStrings.add("Geri Eiler")
        mStrings.add("Tammara Belgrave")
        mStrings.add("Ashton Ridinger")
        mStrings.add("Jodee Dawkins")
        mStrings.add("Florine Cruzan")
        mStrings.add("Latia Stead")
        mStrings.add("Kai Urbain")
        mStrings.add("Liza Chi")
        mStrings.add("Clayton Laprade")
        mStrings.add("Wilfredo Mooney")
        mStrings.add("Roseline Cain")
        mStrings.add("Chadwick Gauna")
        mStrings.add("Carmela Bourn")
        mStrings.add("Valeri Dedios")
        mStrings.add("Calista Mcneese")
        mStrings.add("Willard Cuccia")
        mStrings.add("Ngan Blakey")
        mStrings.add("Reina Medlen")
        mStrings.add("Fabian Steenbergen")
        mStrings.add("Edmond Pine")
        mStrings.add("Teri Quesada")
        mStrings.add("Vernetta Fulgham")
        mStrings.add("Winnifred Kiefer")
        mStrings.add("Chiquita Lichty")
        mStrings.add("Elna Stiltner")
        mStrings.add("Carly Landon")
        mStrings.add("Hans Morford")
        mStrings.add("Shawanna Kapoor")
        mStrings.add("Thomasina Naron")
        mStrings.add("Melba Massi")
        mStrings.add("Sal Mangano")
        mStrings.add("Mika Weitzel")
        mStrings.add("Phylis France")
        mStrings.add("Illa Winzer")
        mStrings.add("Kristofer Boyden")
        mStrings.add("Idalia Cryan")
        mStrings.add("Jenni Sousa")
        mStrings.add("Eda Forkey")
        mStrings.add("Birgit Rispoli")
        mStrings.add("Janiece Mcguffey")
        mStrings.add("Barton Busick")
        mStrings.add("Gerald Westerman")
        mStrings.add("Shalanda Baran")
        mStrings.add("Margherita Pazos")
        mStrings.add("Yuk Fitts")
    }
    fun hideKeyBoard(view: View) {
        hideSoftKeyboard()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(selectedImage!!,
                    filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
        }
    }

    override fun loadImage(imageFile: File, ivImage: ImageView) {
        Glide.with(this).load(imageFile).into(ivImage)
    }

    override fun onMultiImageSelected(uriList: List<Uri>, tag: String) {
        for (i in 0..uriList.size) {
            if (i >= 5) return
            val iv: ImageView?
            when (i) {
                0 -> iv = img1
                1 -> iv = img2
                2 -> iv = img3
                3 -> iv = img4
                4 -> iv = img5
                else -> iv = img5
            }
            Glide.with(this).load(uriList[i]).into(iv!!)
        }
    }

    override fun onSingleImageSelected(uri: Uri, tag: String) {

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