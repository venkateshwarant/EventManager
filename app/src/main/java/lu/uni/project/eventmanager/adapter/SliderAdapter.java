package lu.uni.project.eventmanager.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

import lu.uni.project.eventmanager.R;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<Uri> bitmapList;
    private boolean isWithFirebaseStorage= false;
    private List<StorageReference> storageReferenceList;

    public SliderAdapter(Context context, List<Uri> bitmapList) {
        this.context = context;
        this.bitmapList = bitmapList;
    }

    public SliderAdapter(Context context, List<StorageReference> storageReferenceList, boolean isWithFirebaseStorage) {
        this.context = context;
        this.storageReferenceList = storageReferenceList;
        this.isWithFirebaseStorage= isWithFirebaseStorage;
    }



    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(final SliderAdapterVH viewHolder, final int position) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
        if(isWithFirebaseStorage){
            storageReferenceList.get(position).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(viewHolder.itemView)
                            .load(uri)
                            .fitCenter()
                            .error(R.drawable.ic_image)
                            .dontAnimate()
                            .into(viewHolder.imageViewBackground);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }else {
            Glide.with(viewHolder.itemView)
                    .load(bitmapList.get(position))
                    .fitCenter()
                    .dontAnimate()
                    .into(viewHolder.imageViewBackground);
        }
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        if (isWithFirebaseStorage){
            return storageReferenceList.size();
        }else{
            return bitmapList.size();
        }
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }


}
