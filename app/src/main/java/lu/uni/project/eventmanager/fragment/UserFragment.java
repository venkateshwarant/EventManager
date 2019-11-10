package lu.uni.project.eventmanager.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.hdodenhof.circleimageview.CircleImageView;
import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.util.ImageHelper;
import lu.uni.project.eventmanager.util.PreferenceKeys;
import lu.uni.project.eventmanager.util.SharedPreferencesHelper;

public class UserFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserFragment() {
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root= inflater.inflate(R.layout.fragment_user, container, false);
        if(getContext()!=null){
            ImageLoader imageLoader = ImageLoader.getInstance();
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
            Object imageURI= SharedPreferencesHelper.get(getContext(), PreferenceKeys.profilePhotoURI, "");
            imageLoader.loadImage(imageURI.toString(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    BitmapDrawable ob = new BitmapDrawable(getResources(), ImageHelper.getRoundedCornerBitmap(loadedImage,2048));
                    ImageView profileImage= root.findViewById(R.id.profile_image);
                    profileImage.setImageDrawable(ob);
                }
            });
        }
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
}
