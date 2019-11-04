package lu.uni.project.eventmanager.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.fragment.HeartFragment;
import lu.uni.project.eventmanager.fragment.HomeFragment;
import lu.uni.project.eventmanager.fragment.SearchFragment;
import lu.uni.project.eventmanager.fragment.UserFragment;

public class BottomNavigationActivity extends AppCompatActivity implements
        View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        HeartFragment.OnFragmentInteractionListener,
        UserFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        findViewById(R.id.home_btm_ic).setOnClickListener(this);
        findViewById(R.id.search_btm_ic).setOnClickListener(this);
        findViewById(R.id.add_btm_ic).setOnClickListener(this);
        findViewById(R.id.heart_btm_ic).setOnClickListener(this);
        findViewById(R.id.user_btm_ic).setOnClickListener(this);
        HomeFragment homeFrag = new HomeFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, homeFrag);
        fragmentTransaction.commit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_selected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_heart)).setImageDrawable(getDrawable(R.drawable.ic_heart_unselected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
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
            ((ImageView) findViewById(R.id.ic_heart)).setImageDrawable(getDrawable(R.drawable.ic_heart_unselected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.search_btm_ic){
            SearchFragment searchFrag= new SearchFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, searchFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_selected));
            ((ImageView) findViewById(R.id.ic_heart)).setImageDrawable(getDrawable(R.drawable.ic_heart_unselected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.add_btm_ic){
            startActivity(new Intent(this, CreateEventActivity.class));
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_selected));
            ((ImageView) findViewById(R.id.ic_heart)).setImageDrawable(getDrawable(R.drawable.ic_heart_unselected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.heart_btm_ic){
            HeartFragment heartFrag= new HeartFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, heartFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_heart)).setImageDrawable(getDrawable(R.drawable.ic_heart_selected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }else if(v.getId() == R.id.user_btm_ic){
            UserFragment userFrag= new UserFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, userFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_unselected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_heart)).setImageDrawable(getDrawable(R.drawable.ic_heart_unselected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_selected));
        }else{
            HomeFragment homeFrag = new HomeFragment();
            fragmentTransaction.replace(R.id.bottomSheetFragmentHolder, homeFrag);
            ((ImageView) findViewById(R.id.ic_home)).setImageDrawable(getDrawable(R.drawable.ic_home_selected));
            ((ImageView) findViewById(R.id.ic_search)).setImageDrawable(getDrawable(R.drawable.ic_search_unselected));
            ((ImageView) findViewById(R.id.ic_heart)).setImageDrawable(getDrawable(R.drawable.ic_heart_unselected));
            ((ImageView) findViewById(R.id.ic_user)).setImageDrawable(getDrawable(R.drawable.ic_user_unselected));
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
