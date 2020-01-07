package lu.uni.project.eventmanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.activity.LoginActivity;
import lu.uni.project.eventmanager.activity.MyEventsActivity;
import lu.uni.project.eventmanager.activity.SavedEventsActivity;
import lu.uni.project.eventmanager.pojo.Event;
import lu.uni.project.eventmanager.pojo.User;
import lu.uni.project.eventmanager.util.ImageHelper;
import lu.uni.project.eventmanager.util.PreferenceKeys;
import lu.uni.project.eventmanager.util.SharedPreferencesHelper;

public class UserFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public UserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root= inflater.inflate(R.layout.fragment_user, container, false);
        if(getContext()!=null){
            final TextView emailID= root.findViewById(R.id.profileEmailID);
            final TextView phoneNumber= root.findViewById(R.id.profilePhoneNumber);
            final TextView address= root.findViewById(R.id.profileAddress);
            final TextView name= root.findViewById(R.id.profileName);
            final TextView savedEventsCount= root.findViewById(R.id.savedEventsCount);
            final TextView myEventsCount= root.findViewById(R.id.myCount);
            final LinearLayout savedEventsHolder= root.findViewById(R.id.savedEvents);
            final LinearLayout myEventsHolder= root.findViewById(R.id.myEvents);
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                ImageLoader imageLoader = ImageLoader.getInstance();
                ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
                Object imageURI= SharedPreferencesHelper.get(getContext(), PreferenceKeys.profilePhotoURI, "");
                imageLoader.loadImage(imageURI.toString(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if(getContext()!=null){
                            BitmapDrawable ob = new BitmapDrawable(getResources(), ImageHelper.getRoundedCornerBitmap(loadedImage,2048));
                            ImageView profileImage= root.findViewById(R.id.profile_image);
                            profileImage.setImageDrawable(ob);
                        }
                    }
                });
                root.findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferencesHelper.remove(getContext().getApplicationContext(), PreferenceKeys.profilePhotoURI);
                        SharedPreferencesHelper.remove(getContext().getApplicationContext(), PreferenceKeys.profileFirstName);
                        SharedPreferencesHelper.remove(getContext().getApplicationContext(), PreferenceKeys.profileLastName);
                        SharedPreferencesHelper.remove(getContext().getApplicationContext(), PreferenceKeys.profileDisplayName);
                        FirebaseAuth.getInstance().signOut();
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build();
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                        mGoogleSignInClient.signOut();
                        getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("user").child(FirebaseAuth.getInstance().getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            emailID.setText(user.getEmailID());
                            phoneNumber.setText(user.getPhoneNumber());
                            address.setText(user.getAddress());
                            name.setText(user.getDisplayName());
                            ref.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                final DatabaseReference savedEventsRef = database.getReference("saved").child(FirebaseAuth.getInstance().getUid());
                savedEventsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> outputList= new ArrayList();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            String event = postSnapshot.getKey();
                            outputList.add(event);
                        }

                        if(outputList.size()>0){
                            final TextView sc= root.findViewById(R.id.savedEventsCount);

                            sc.setText(Integer.toString(outputList.size()));
                            savedEventsHolder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent= new Intent(UserFragment.this.getActivity(), SavedEventsActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }else{
                            savedEventsCount.setText("0");
                        }
                        savedEventsRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                final DatabaseReference events = database.getReference("event");
                events.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                            List<Event> outputList= new ArrayList();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Event event = postSnapshot.getValue(Event.class);
                                if(event.getUserId().contentEquals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    outputList.add(event);
                                }
                            }
                            if(outputList.size()>0){
                                myEventsCount.setText(Integer.toString(outputList.size()));
                                myEventsHolder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(UserFragment.this.getActivity(), MyEventsActivity.class));
                                    }
                                });
                            }else{
                                myEventsCount.setText("0");
                            }
                        }
                        events.addValueEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }else{
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
            }
        }
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
    public static void changeStatusBarColor(Activity activity ) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.Orange));
            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags = systemUiVisibilityFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }
    }
}
