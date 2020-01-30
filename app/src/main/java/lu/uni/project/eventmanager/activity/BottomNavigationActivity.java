package lu.uni.project.eventmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.fragment.RatingFragment;
import lu.uni.project.eventmanager.fragment.HomeFragment;
import lu.uni.project.eventmanager.fragment.ImageFragment;
import lu.uni.project.eventmanager.fragment.SearchFragment;
import lu.uni.project.eventmanager.fragment.UserFragment;
import lu.uni.project.eventmanager.fragment.VideoFragment;
import lu.uni.project.eventmanager.util.BundleKeys;

public class BottomNavigationActivity extends AppCompatActivity implements
        View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        RatingFragment.OnFragmentInteractionListener,
        UserFragment.OnFragmentInteractionListener,
        ImageFragment.OnFragmentInteractionListener,
        VideoFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        changeStatusBarColor(this);
        findViewById(R.id.home_btm_ic).setOnClickListener(this);
        findViewById(R.id.search_btm_ic).setOnClickListener(this);
        findViewById(R.id.add_btm_ic).setOnClickListener(this);
        findViewById(R.id.star_btm_ic).setOnClickListener(this);
        findViewById(R.id.user_btm_ic).setOnClickListener(this);
        setupUI(findViewById(R.id.container));
        HomeFragment homeFrag = new HomeFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, homeFrag);
        fragmentTransaction.commit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_selected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_star)).setImageDrawable(getDrawable(R.drawable.ic_star_black));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }
        if(getIntent().getStringExtra(BundleKeys.Companion.getEditEventKey())!=null && getIntent().getStringExtra(BundleKeys.Companion.getEditEventKey()).equals("true")){
            Toast.makeText(this, "Event edited!", Toast.LENGTH_SHORT).show();
        }else if(getIntent().getStringExtra("event_created")!=null && getIntent().getStringExtra("event_created").equals("true")){
            Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(v.getId() == R.id.home_btm_ic){
            HomeFragment homeFrag = new HomeFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, homeFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_selected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_star)).setImageDrawable(getDrawable(R.drawable.ic_star_black));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.search_btm_ic){
            SearchFragment searchFrag= new SearchFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, searchFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_selected));
            ((ImageView) findViewById(R.id.ic_star)).setImageDrawable(getDrawable(R.drawable.ic_star_black));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.add_btm_ic){
            startActivity(new Intent(this, CreateEventActivity.class));
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_selected));
            ((ImageView) findViewById(R.id.ic_star)).setImageDrawable(getDrawable(R.drawable.ic_star_black));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.star_btm_ic){
            RatingFragment heartFrag= new RatingFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, heartFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_star)).setImageDrawable(getDrawable(R.drawable.ic_star_black_selected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.user_btm_ic){
            UserFragment userFrag= new UserFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, userFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_star)).setImageDrawable(getDrawable(R.drawable.ic_star_black));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_selected));
        }else{
            HomeFragment homeFrag = new HomeFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, homeFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_selected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_star)).setImageDrawable(getDrawable(R.drawable.ic_star_black));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123){

        }
    }

    public void changeStatusBarColor(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorWhite));
            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags = systemUiVisibilityFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        if(activity!=null & activity.getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(BottomNavigationActivity.this);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
