package lu.uni.project.eventmanager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import lu.uni.project.eventmanager.fragment.ImageFragment;
import lu.uni.project.eventmanager.fragment.VideoFragment;

public class MultimediaAdapter extends FragmentStatePagerAdapter {

    private List<StorageReference> firestorage;
    public MultimediaAdapter(@NonNull FragmentManager fm, List<StorageReference> storage) {
        super(fm);
        firestorage= storage;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==1){
            return ImageFragment.newInstance("","", firestorage);
        }else{
            return VideoFragment.newInstance("","");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
