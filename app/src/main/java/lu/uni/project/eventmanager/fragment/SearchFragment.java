package lu.uni.project.eventmanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class SearchFragment extends Fragment implements AbsListView.OnScrollListener  {

    private OnFragmentInteractionListener mListener;

    private ListView listView;
    private EventsAdapter adapter;
    private Handler mHandler;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        changeStatusBarColor(getActivity());
        final View root= inflater.inflate(R.layout.fragment_search, container, false);
        final EditText edit_txt = root.findViewById(R.id.searchInput);
        listView= root.findViewById(R.id.filterList);
        edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String searchQuery= edit_txt.getText().toString();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference ref = database.getReference("event");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Event> outputList= new ArrayList();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Event event = postSnapshot.getValue(Event.class);
                                if(event.getEventName().toLowerCase().contains(searchQuery.toLowerCase())||event.getEventCategory().toLowerCase().contains(searchQuery.toLowerCase())||event.getEventDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                                    outputList.add(event);
                                }
                            }
                            FragmentActivity act = getActivity();
                            if(act!=null){
                            adapter = new EventsAdapter(act,outputList , 20, 10);
                            listView.setAdapter(adapter);
                            listView.setOnScrollListener(SearchFragment.this); //listen for a scroll movement to the bottom
                            ref.removeEventListener(this);}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        mHandler = new Handler();
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
                listView.setOnScrollListener(SearchFragment.this); //listen for a scroll movement to the bottom
                ref.removeEventListener(this);}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return root;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem + visibleItemCount == totalItemCount && !adapter.endReached() && !hasCallback){ //check if we've reached the bottom
            hasCallback = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private boolean hasCallback;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
