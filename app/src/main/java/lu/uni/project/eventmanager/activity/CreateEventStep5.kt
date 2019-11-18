package lu.uni.project.eventmanager.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.schibstedspain.leku.*
import kotlinx.android.synthetic.main.activity_create_event_step5.*
import lu.uni.project.eventmanager.pojo.Event
import lu.uni.project.eventmanager.pojo.Location
import lu.uni.project.eventmanager.util.BundleKeys
import lu.uni.project.eventmanager.R
import lu.uni.project.eventmanager.util.GlobalUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit


class CreateEventStep5 : AppCompatActivity() {
    val MAP_BUTTON_REQUEST_CODE= 1
    var lat:String=""
    var lon:String=""
    var addr:String=""
    var zipc:String=""
    var inAnimation: AlphaAnimation? = null
    var outAnimation: AlphaAnimation? = null
    var progressBarHolder: FrameLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event_step5)
        pickLocationButton.setOnClickListener{
            val locationPickerIntent = LocationPickerActivity.Builder()
                    .withGeolocApiKey("AIzaSyBr5l_bqBZWFO9W2Ys3HrNRwF0_9628KYo")
//                    .shouldReturnOkOnBackPressed()
                    .withZipCodeHidden()
                    .withGooglePlacesEnabled()
                    .withGoogleTimeZoneEnabled()
                    .build(applicationContext)

            startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
        }
        next.setOnClickListener{
            var event= intent.extras?.getBundle(BundleKeys.event)
            event?.putString(BundleKeys.addressKey,  addr)
            event?.putString(BundleKeys.lattitudeKey, lat)
            event?.putString(BundleKeys.longitudeKey, lon)
            event?.putString(BundleKeys.zipcodeKey, zipc)

            var eventObj= Event()
            eventObj.eventName=event?.getString(BundleKeys.eventNameKey)
            eventObj.eventDescription=event?.getString(BundleKeys.eventDescriptionKey)
            eventObj.eventCategory=event?.getString(BundleKeys.eventCategoryKey)
            eventObj.startTime=event?.getString(BundleKeys.startTimeKey)
            eventObj.startTime=event?.getString(BundleKeys.startTimeKey)
            eventObj.endTime=event?.getString(BundleKeys.endTimeKey)
            eventObj.startDate=event?.getString(BundleKeys.startDateKey)
            eventObj.endDate=event?.getString(BundleKeys.endDateKey)
            eventObj.images=event?.getString(BundleKeys.imagesListKey)
            eventObj.location= Location()
            eventObj.location.setAddress(addr)
            eventObj.location.setLatitude(lat)
            eventObj.location.setLongitude(lon)
            eventObj.location.zipCode= zipc
            eventObj.location.venueDetails=findViewById<TextView>(R.id.venueDetails).text.toString()
            eventObj.setUserId(FirebaseAuth.getInstance().currentUser?.uid)
            eventObj.setCreatedTime(System.currentTimeMillis().toString())
            UpdateTask().execute(eventObj)



//            var intent= Intent(this, CreateEventStep5::class.java)
//            startActivity(intent)
//            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
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
        progressBarHolder =  findViewById(R.id.progressBarHolder)
        changeStatusBarColor(this)
    }
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK")
            if (requestCode == 1) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                lat=latitude.toString()
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                lon=longitude.toString()
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                addr= address
                Log.d("ADDRESS****", address?.toString())
                val postalcode = data.getStringExtra(ZIPCODE)
                zipc= postalcode
                Log.d("POSTALCODE****", postalcode?.toString())
//                val bundle = data.getBundleExtra(TRANSITION_BUNDLE)
//                Log.d("BUNDLE TEXT****", bundle?.getString("test"))
                val fullAddress = data.getParcelableExtra<Address>(ADDRESS)
                if (fullAddress != null) {
                    Log.d("FULL ADDRESS****", fullAddress.toString())
                }
//                val timeZoneId = data.getStringExtra(TIME_ZONE_ID)
//                Log.d("TIME ZONE ID****", timeZoneId)
//                val timeZoneDisplayName = data.getStringExtra(TIME_ZONE_DISPLAY_NAME)
//                Log.d("TIME ZONE NAME****", timeZoneDisplayName)
                pickLocationButton.visibility= View.GONE
                addressLayout.visibility= View.VISIBLE
                addressText.text= address?.toString()
                addressLayout.setOnClickListener{
                    val locationPickerIntent = LocationPickerActivity.Builder()
//                    .withLocation(41.4036299, 2.1743558)
                            .withGeolocApiKey("AIzaSyBr5l_bqBZWFO9W2Ys3HrNRwF0_9628KYo")
//                    .withSearchZone("es_ES")
//                    .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
//                    .withDefaultLocaleSearchZone()
                            .shouldReturnOkOnBackPressed()
//                    .withStreetHidden()
//                    .withCityHidden()
                            .withZipCodeHidden()
//                    .withSatelliteViewHidden()
//                    .withMapStyle(R.style.Theme_AppCompat_Map)
//                    .withMapStyle(R.raw.map_style_retro)

                            .withGooglePlacesEnabled()
                            .withGoogleTimeZoneEnabled()
//                    .withVoiceSearchHidden()
//                    .withUnnamedRoadHidden()
                            .build(applicationContext)

                    startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
                }
            } else if (requestCode == 2) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                Log.d("ADDRESS****", address?.toString())
                val lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
                Log.d("LekuPoi****", lekuPoi?.toString())
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
    }
    companion object{
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

        fun getURIListFromString(uriStrList:String): MutableList<Uri> {
            var uriArr= uriStrList.replace("[","").replace("]","").split(",")
            var output= mutableListOf<Uri>()
            for (uri in uriArr){
                output.add(Uri.parse(uri))
            }
            return output
        }
    }
    private inner class UpdateTask : AsyncTask<Event, Void?, Void?>() {
        override fun doInBackground(vararg params: Event?): Void? {
            try {
                var db= FirebaseDatabase.getInstance().getReference("event")
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                var uid = ""
                if (user != null) {
                    uid = user.uid
                }
                var key= db.push().key!!
                var event=params[0]
                event?.eventId= key
                event?.imagesCount= GlobalUtil.imagesList?.size
                db.child(key).setValue(event)
                val storage = FirebaseStorage.getInstance()
                var storageRef = storage.reference
                var imagesRef: StorageReference? = storageRef.child("images/"+event?.eventId+"/")
                for(i in 0 until GlobalUtil.imagesList?.size!!){
                    val stream = FileInputStream(GlobalUtil.imagesList!![i].toFile())
                    var img= imagesRef?.child("$i.jpg")
                    var uploadTask = img?.putStream(stream)
                    uploadTask?.addOnFailureListener {
                        // Handle unsuccessful uploads
                    }?.addOnSuccessListener {
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        // ...
                    }
                }
                var intent= Intent(this@CreateEventStep5, BottomNavigationActivity::class.java)
                intent.putExtra("event_created","true")
                startActivity(intent)
                this@CreateEventStep5.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
//            button.setEnabled(false)
            inAnimation = AlphaAnimation(0f, 1f)
            inAnimation?.duration = 200
            progressBarHolder?.animation = inAnimation
            progressBarHolder?.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            outAnimation = AlphaAnimation(1f, 0f)
            outAnimation?.duration = 200
            progressBarHolder?.animation = outAnimation
            progressBarHolder?.visibility = View.GONE
//            button.setEnabled(true)
        }

    }
}
