package lu.uni.project.eventmanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.Service.GPSTracker;
import lu.uni.project.eventmanager.adapter.EventsAdapter;
import lu.uni.project.eventmanager.bottomsheet.FillterBottomSheetFragment;
import lu.uni.project.eventmanager.pojo.Event;

public class HomeFragment extends Fragment implements AbsListView.OnScrollListener {


    private OnFragmentInteractionListener mListener;

    private ListView listView;
    private ProgressBar progressBar;
    private EventsAdapter adapter;
    private Handler mHandler;
    SwipeRefreshLayout mySwipeRefreshLayout=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root= inflater.inflate(R.layout.fragment_home, container, false);
        mySwipeRefreshLayout= root.findViewById(R.id.swipableContent);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(true);
                        loadItems(root);

                    }
                }
        );
        mHandler = new Handler();
        View footer = getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
        progressBar = footer.findViewById(R.id.progressBar);

        listView = root.findViewById(R.id.listView);
        listView.addFooterView(footer);
        mySwipeRefreshLayout.setRefreshing(true);

        root.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillterBottomSheetFragment btmSheet= new FillterBottomSheetFragment(new FillterBottomSheetFragment.OnFilterSelectListener() {
                    @Override
                    public void onSelectDistanceRange(final int range) {
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference ref = database.getReference("event");
                        ref.addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<Event> outputList= new ArrayList();
                                List<Event> newList= new ArrayList();
                                Context context= getContext();
                                if(context!=null){
                                    GPSTracker gpsTracker = new GPSTracker(context);
                                    gpsTracker.getLocation();
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        Event event = postSnapshot.getValue(Event.class);
                                        outputList.add(event);
                                        if(distance(Double.parseDouble(event.getLocation().getLatitude()),Double.parseDouble(event.getLocation().getLongitude()), gpsTracker.getLatitude(), gpsTracker.getLongitude())<=range){
                                            newList.add(event);
                                        }

                                    }
                                    FragmentActivity act = getActivity();
                                    if(act!=null){
                                        adapter = new EventsAdapter(act,newList , 20, 10);
                                        listView.setAdapter(adapter);
                                        progressBar.setVisibility((20 < outputList.size())? View.VISIBLE : View.GONE);
                                        listView.setOnScrollListener(HomeFragment.this); //listen for a scroll movement to the bottom
                                        mySwipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                                ref.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }

                    @NotNull
                    @Override
                    public void onSelectDateRange(@NotNull final Date from, @NotNull final Date to) {
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference ref = database.getReference("event");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<Event> outputList= new ArrayList();
                                List<Event> newList= new ArrayList();
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    Event event = postSnapshot.getValue(Event.class);
                                    outputList.add(event);
                                    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date startDate= formatter.parse(event.getStartDate());
                                        Date endDate= formatter.parse(event.getEndDate());
                                        if( endDate.before(to) &&endDate.after(from) || startDate.before(to)&&startDate.after(from)  || endDate.before(to)&&startDate.after(from)){
                                            newList.add(event);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                                FragmentActivity act = getActivity();
                                if(act!=null){
                                adapter = new EventsAdapter(act,newList , 20, 10);
                                listView.setAdapter(adapter);
                                progressBar.setVisibility((20 < outputList.size())? View.VISIBLE : View.GONE);
                                listView.setOnScrollListener(HomeFragment.this); //listen for a scroll movement to the bottom
                                mySwipeRefreshLayout.setRefreshing(false);
                                ref.removeEventListener(this);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
                btmSheet.show(getActivity().getSupportFragmentManager(),"");
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("event");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> outputList= new ArrayList();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    outputList.add(event);
                }
                FragmentActivity act = getActivity();
                if(act!=null){
                adapter = new EventsAdapter(act,outputList , 20, 10);
                listView.setAdapter(adapter);
                progressBar.setVisibility((20 < outputList.size())? View.VISIBLE : View.GONE);
                listView.setOnScrollListener(HomeFragment.this); //listen for a scroll movement to the bottom
                mySwipeRefreshLayout.setRefreshing(false);
                ref.removeEventListener(this);}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });
        changeStatusBarColor(getActivity());
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem + visibleItemCount == totalItemCount && !adapter.endReached() && !hasCallback){ //check if we've reached the bottom
//            mHandler.postDelayed(showMore, 300);
            hasCallback = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private boolean hasCallback;
    private void loadItems(final View view) {
        if(view!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler = new Handler();
                    mySwipeRefreshLayout.setRefreshing(true);

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference ref = database.getReference("event");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Event> outputList= new ArrayList();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Event event = postSnapshot.getValue(Event.class);
                                outputList.add(event);
                            }
                            FragmentActivity act = getActivity();
                            if(act!=null){
                            adapter = new EventsAdapter(act,outputList , 20, 10);
                            listView.setAdapter(adapter);
                            progressBar.setVisibility((20 < outputList.size())? View.VISIBLE : View.GONE);
                            listView.setOnScrollListener(HomeFragment.this); //listen for a scroll movement to the bottom
                            mySwipeRefreshLayout.setRefreshing(false);
                            ref.removeEventListener(this);}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }, 3000);
        }
    }
    public static void changeStatusBarColor(Activity activity ) {
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
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
