package lu.uni.project.eventmanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.activity.LoginActivity;
import lu.uni.project.eventmanager.adapter.ViewRatingsNotificationAdapter;
import lu.uni.project.eventmanager.pojo.RatingNotification;

public class RatingFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public RatingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        changeStatusBarColor(getActivity());
        View root=inflater.inflate(R.layout.fragment_heart, container, false);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final ListView listView = root.findViewById(R.id.ratingList);
            if (FirebaseAuth.getInstance().getUid() != null && !FirebaseAuth.getInstance().getUid().contentEquals("")) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference userRef = database.getReference("ratingNotification").child(FirebaseAuth.getInstance().getUid());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<RatingNotification> ratingNotifications = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            RatingNotification rating = postSnapshot.getValue(RatingNotification.class);
                            ratingNotifications.add(rating);
                        }
                        FragmentActivity act = getActivity();
                        if (act != null) {
                            listView.setAdapter(new ViewRatingsNotificationAdapter(act, ratingNotifications));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }else{
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
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

    public static void changeStatusBarColor(Activity activity ) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorButtonBlack));
            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags = systemUiVisibilityFlags & invertBits(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }

    }

    static int invertBits(int num) {
        int x = (int)(Math.log(num) / Math.log(2)) + 1;
        for (int i = 0; i < x; i++)
            num = (num ^ (1 << i));
        return num;
    }
}
