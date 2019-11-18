package lu.uni.project.eventmanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.adapter.EventsAdapter;
import lu.uni.project.eventmanager.bottomsheet.FillterBottomSheetFragment;
import lu.uni.project.eventmanager.pojo.Event;

public class HomeFragment extends Fragment implements AbsListView.OnScrollListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView listView;
    private ProgressBar progressBar;
    private EventsAdapter adapter;
    private Handler mHandler;
    SwipeRefreshLayout mySwipeRefreshLayout=null;
    public HomeFragment HomeFragment() {
        return new HomeFragment();
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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

        //inflate the progress bar from the footer, it is wrapped in a FrameLayout since
        //ListViews don't shrink in height when a child is set to visibility gone, but
        //a FrameLayout with height of wrap_content will
        View footer = getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
        progressBar = (ProgressBar) footer.findViewById(R.id.progressBar);

        listView = (ListView) root.findViewById(R.id.listView);
        listView.addFooterView(footer);
        mySwipeRefreshLayout.setRefreshing(true);

        root.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillterBottomSheetFragment btmSheet= new FillterBottomSheetFragment();
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
                adapter = new EventsAdapter(getActivity(),outputList , 20, 10);
                listView.setAdapter(adapter);
                progressBar.setVisibility((20 < outputList.size())? View.VISIBLE : View.GONE);
                listView.setOnScrollListener(HomeFragment.this); //listen for a scroll movement to the bottom
                mySwipeRefreshLayout.setRefreshing(false);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });
        changeStatusBarColor(getActivity());
        return root;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    private Runnable showMore = new Runnable(){
        public void run(){
            boolean noMoreToShow = adapter.showMore(); //show more views and find out if
            progressBar.setVisibility(noMoreToShow? View.GONE : View.VISIBLE);
            hasCallback = false;
        }
    };
    private void loadItems(final View view) {
        if(view!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler = new Handler();
//                    View footer = getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
//                    listView.addFooterView(footer);
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
                            adapter = new EventsAdapter(getActivity(),outputList , 20, 10);
                            listView.setAdapter(adapter);
                            progressBar.setVisibility((20 < outputList.size())? View.VISIBLE : View.GONE);
                            listView.setOnScrollListener(HomeFragment.this); //listen for a scroll movement to the bottom
                            mySwipeRefreshLayout.setRefreshing(false);
                            ref.removeEventListener(this);
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
}
