package lu.uni.project.eventmanager.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.schibstedspain.leku.LocationPickerActivity;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.activity.CreateEventActivity;
import lu.uni.project.eventmanager.bottomsheet.ParticipantsBottomSheetFragment;
import lu.uni.project.eventmanager.bottomsheet.ViewAllCommentsBottomSheetFragment;
import lu.uni.project.eventmanager.dialog.RateEventDialog;
import lu.uni.project.eventmanager.fragment.HomeFragment;
import lu.uni.project.eventmanager.pojo.Comment;
import lu.uni.project.eventmanager.pojo.Event;
import lu.uni.project.eventmanager.pojo.User;
import lu.uni.project.eventmanager.util.BundleKeys;
import lu.uni.project.eventmanager.util.ImageHelper;
import lu.uni.project.eventmanager.util.PreferenceKeys;
import lu.uni.project.eventmanager.util.SharedPreferencesHelper;


public class EventsAdapter extends ArrayAdapter<Event>{

	private FragmentActivity context;
	private List<Event> values;
	private int count;
	private int stepNumber;
	private int startCount;
	private int MAP_BUTTON_REQUEST_CODE= 1;
	private int EDIT_EVENT_REQUEST_CODE= 1;

	/**
	 *
	 * @param context
	 * @param startCount the initial number of views to show
	 * @param stepNumber the number of additional views to show with each expansion
	 */
	public EventsAdapter(FragmentActivity context,List<Event> values, int startCount, int stepNumber) {
		super(context, R.layout.row_layout, values);
		this.context = context;
		this.values = values;
		this.startCount = Math.min(startCount, values.size()); //don't try to show more views than we have
		this.count = this.startCount;
		this.stepNumber = stepNumber;
	}

	public List<StorageReference> getEventImageStorageReference(Event event){
        final List<StorageReference> imgRefList= new ArrayList<>();
        for(int i=0; i<event.imagesCount; i++){
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference imgRef = mStorageRef.child("images/"+event.getEventId()+"/"+i+".jpg");
            imgRefList.add(imgRef);
        }
        return imgRefList;
    }

	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if(view == null){
			view = LayoutInflater.from(context).inflate(R.layout.row_layout, null);
            holder = new ViewHolder();
            holder.eventName= view.findViewById(R.id.eventName);
            holder.name= view.findViewById(R.id.name);
            holder.profileImage= view.findViewById(R.id.profile_image);
            holder.profileImageComment= view.findViewById(R.id.userProfileImgComment);
            holder.detailsEventName= view.findViewById(R.id.detailsEventName);
            holder.detailsEventDescription= view.findViewById(R.id.detailsEventDescrition);
            holder.detailsEventType= view.findViewById(R.id.detailsEventType);
            holder.detailsStartDate= view.findViewById(R.id.detailsStartDate);
            holder.detailsEndDate= view.findViewById(R.id.detailsEndDate);
            holder.detailsContactEmailID= view.findViewById(R.id.detailsContactEmail);
            holder.detailsContactPhoneNumber= view.findViewById(R.id.detailscontactPhNo);
            holder.detailsAddress= view.findViewById(R.id.detailsLocationAddress);
            holder.detailsVenue= view.findViewById(R.id.detailsVenueDetails);
            holder.location= view.findViewById(R.id.location);
            holder.direction= view.findViewById(R.id.direction);
            holder.menuLayout= view.findViewById(R.id.eventMenuIcon);
            holder.rateEvent= view.findViewById(R.id.rate);
            holder.rateImage= view.findViewById(R.id.starImage);
            holder.rateCount= view.findViewById(R.id.rateCount);
            holder.viewAllComments= view.findViewById(R.id.viewAllComments);
            holder.comment= view.findViewById(R.id.commentBox);
            holder.eventBox= view.findViewById(R.id.eventBox);
            holder.postComment= view.findViewById(R.id.commentBtn);
            holder.eventDetails= view.findViewById(R.id.eventDetailsScrollPort);
            holder.save= view.findViewById(R.id.save);
            holder.saveText= view.findViewById(R.id.saveText);
            holder.participantsCount= view.findViewById(R.id.participantsCount);
            holder.saveImage= view.findViewById(R.id.saveImg);
            holder.participants= view.findViewById(R.id.participants);
            holder.viewCount= view.findViewById(R.id.viewCount);
            view.setTag(holder);
		}else{
            holder = (ViewHolder) view.getTag();
		}
        holder.eventName.setText(values.get(position).getEventName());
        holder.detailsEventName.setText(values.get(position).getEventName());
        holder.detailsEventDescription.setText(values.get(position).getEventDescription());
        holder.detailsEventType.setText(values.get(position).getEventCategory());
        holder.detailsStartDate.setText(values.get(position).getStartDate());
        holder.detailsEndDate.setText(values.get(position).getEndDate());
        holder.detailsContactEmailID.setText("");
        holder.detailsContactPhoneNumber.setText("");
        holder.detailsAddress.setText(values.get(position).getLocation().getAddress());
        holder.detailsVenue.setText(values.get(position).getLocation().getVenueDetails());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        final String uid = user.getUid();


        final DatabaseReference viewDB= FirebaseDatabase.getInstance().getReference("views").child(values.get(position).getEventId()).child(user.getUid());
        viewDB.setValue(0);

        final DatabaseReference regDB= FirebaseDatabase.getInstance().getReference("views").child(values.get(position).getEventId());
        regDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    i++;
                }
                holder.viewCount.setText(Integer.toString(i));
                viewDB.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final DatabaseReference userRef = database.getReference("user").child(values.get(position).getUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user= dataSnapshot.getValue(User.class);
                if(user!=null){
                    holder.name.setText(user.getDisplayName());
                    RetrieveProfileImage task= new RetrieveProfileImage();
                    task.execute(user.getProfileImgURL(), holder.profileImage, context);
                    RetrieveProfileImage task2= new RetrieveProfileImage();
                    Object imageURI= SharedPreferencesHelper.get(getContext(), PreferenceKeys.profilePhotoURI, "");
                    task2.execute(imageURI, holder.profileImageComment, context);
                    final Boolean[] isRegistered = {false};
                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("register").child(values.get(position).getEventId());
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null){
                                isRegistered[0] =true;
                            }else{
                                isRegistered[0] =false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    holder.menuLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(user.getUid()!=null&& FirebaseAuth.getInstance().getCurrentUser()!=null){
                                final PopupMenu popupMenu = new PopupMenu(context, holder.menuLayout);
                                if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    popupMenu.getMenu().add("Delete event");
                                    popupMenu.getMenu().add("Edit event");
                                }else if(isRegistered[0]){
                                    popupMenu.getMenu().add("Unregister");
                                }else{
                                    popupMenu.getMenu().add("Register");
                                }

                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        if(item.getTitle().toString().equals("Delete event")){
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Discard event?")
                                                    .setMessage("Are you sure you want to delete this event?")
                                                    .setPositiveButton(android.R.string.no, null)
                                                    .setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            holder.eventBox.setVisibility(View.GONE);
                                                            FirebaseDatabase.getInstance().getReference("event").child(values.get(position).getEventId()).removeValue();
                                                            FirebaseDatabase.getInstance().getReference("rating").child(values.get(position).getEventId()).removeValue();
                                                            FirebaseDatabase.getInstance().getReference("comment").child(values.get(position).getEventId()).removeValue();
                                                            for(int i=0;i<values.get(position).imagesCount;i++){
                                                                FirebaseStorage.getInstance().getReference().child("images").child(values.get(position).getEventId()).child(i+".jpg").delete();
                                                            }
                                                            Toast.makeText(getContext(),"Event deleted!", Toast.LENGTH_SHORT).show();
                                                            reloadData();
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }else if(item.getTitle().toString().equals("Edit event")){
                                            Bundle bundle= new Bundle();
                                            bundle.putString(BundleKeys.Companion.getEventIDKey(), values.get(position).eventId);
                                            bundle.putString(BundleKeys.Companion.getEventNameKey(), values.get(position).eventName);
                                            bundle.putString(BundleKeys.Companion.getEventCreatedTimeKey(), values.get(position).createdTime);
                                            bundle.putString(BundleKeys.Companion.getEndDateKey(), values.get(position).endDate);
                                            bundle.putString(BundleKeys.Companion.getEndTimeKey(), values.get(position).endTime);
                                            bundle.putString(BundleKeys.Companion.getEventCategoryKey(), values.get(position).eventCategory);
                                            bundle.putString(BundleKeys.Companion.getEventDescriptionKey(), values.get(position).eventDescription);
                                            bundle.putString(BundleKeys.Companion.getImagesListKey(), values.get(position).images);
                                            bundle.putString(BundleKeys.Companion.getImagesCountKey(), Integer.toString(values.get(position).imagesCount));
                                            bundle.putString(BundleKeys.Companion.getAddressKey(), values.get(position).location.address);
                                            bundle.putString(BundleKeys.Companion.getLattitudeKey(), values.get(position).location.latitude);
                                            bundle.putString(BundleKeys.Companion.getLongitudeKey(), values.get(position).location.longitude);
                                            bundle.putString(BundleKeys.Companion.getVenueDetailsKey(), values.get(position).location.venueDetails);
                                            bundle.putString(BundleKeys.Companion.getZipcodeKey(), values.get(position).location.zipCode);
                                            bundle.putString(BundleKeys.Companion.getEditEventKey(), "true");
                                            Intent intent= new Intent(context, CreateEventActivity.class);
                                            intent.putExtras(bundle);
                                            context.startActivityForResult(intent, EDIT_EVENT_REQUEST_CODE);
                                            context.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                                        }else if(item.getTitle().toString().equals("Register")){
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Register event?")
                                                    .setMessage("Are you sure you want to register for this event?")
                                                    .setPositiveButton(android.R.string.no, null)
                                                    .setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            FirebaseAuth auth = FirebaseAuth.getInstance();
                                                            FirebaseUser user = auth.getCurrentUser();
                                                            DatabaseReference db= FirebaseDatabase.getInstance().getReference("register").child(values.get(position).getEventId()).child(user.getUid());
                                                            db.setValue(0);
                                                            final DatabaseReference regDB= FirebaseDatabase.getInstance().getReference("register").child(values.get(position).getEventId());
                                                            regDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    int i=0;
                                                                    for(DataSnapshot child:dataSnapshot.getChildren()){
                                                                        i++;
                                                                    }
                                                                    holder.participantsCount.setText(Integer.toString(i));
                                                                    regDB.removeEventListener(this);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }else if(item.getTitle().toString().equals("Unregister")){
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Unregister event?")
                                                    .setMessage("Are you sure you want to unregister for this event?")
                                                    .setPositiveButton(android.R.string.no, null)
                                                    .setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            FirebaseAuth auth = FirebaseAuth.getInstance();
                                                            FirebaseUser user = auth.getCurrentUser();
                                                            final DatabaseReference regDB= FirebaseDatabase.getInstance().getReference("register").child(values.get(position).getEventId());
                                                            regDB.removeValue();
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                        return false;
                                    }
                                });
                                popupMenu.show();
                            }
                        }
                    });
                }
                userRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ref = database.getReference("saved").child(uid).child(values.get(position).getEventId());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ref.removeEventListener(this);
                        if(dataSnapshot.getValue()==null){
                            ref.setValue(0);
                            holder.saveText.setText("Saved");
                            holder.saveImage.setImageDrawable(context.getDrawable(R.drawable.ic_bookmarked));
                            Toast.makeText(context, "Event saved", Toast.LENGTH_SHORT).show();
                        }else {
                            ref.removeValue();
                            holder.saveText.setText("Save");
                            holder.saveImage.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark));
                            Toast.makeText(context, "Deleted from saved events!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        final DatabaseReference regisDB= FirebaseDatabase.getInstance().getReference("register").child(values.get(position).getEventId());
        regisDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    i++;
                }
                holder.participantsCount.setText(Integer.toString(i));
                regisDB.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref = database.getReference("rating").child(values.get(position).getEventId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        holder.rateCount.setText(postSnapshot.getValue().toString());
                        holder.rateImage.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled));
                        break;
                    }
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference saveRef = database.getReference("saved").child(uid);
                saveRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals(values.get(position).getEventId())){
                                holder.saveText.setText("Saved");
                                holder.saveImage.setImageDrawable(context.getDrawable(R.drawable.ic_bookmarked));
                            }
                        }
                        saveRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.viewAllComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewAllCommentsBottomSheetFragment btmSheet= new ViewAllCommentsBottomSheetFragment(values.get(position));
                btmSheet.show(context.getSupportFragmentManager(), "");
            }
        });

        holder.participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParticipantsBottomSheetFragment btmSheet= new ParticipantsBottomSheetFragment(values.get(position));
                btmSheet.show(context.getSupportFragmentManager(), "");
            }
        });

        holder.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db= FirebaseDatabase.getInstance().getReference("comment");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                String uid = "";
                if (user != null) {
                    uid = user.getUid();
                }
                Comment commentObj= new Comment();
                commentObj.setComment(holder.comment.getText().toString());
                commentObj.setCreatedTime(Long.toString(System.currentTimeMillis()));
                commentObj.setUserID(uid);
                commentObj.setEventID(values.get(position).eventId);
                commentObj.setCommentID(db.push().getKey());
                db.child(values.get(position).getEventId()).child(commentObj.getCommentID()).setValue(commentObj);
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                holder.comment.setText("");
                Toast.makeText(context,"Comment added!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationPickerIntent = new LocationPickerActivity.Builder()
                        .withGeolocApiKey("AIzaSyBr5l_bqBZWFO9W2Ys3HrNRwF0_9628KYo")
                        .withLocation(Float.parseFloat(values.get(position).getLocation().getLatitude()), Float.parseFloat(values.get(position).getLocation().getLongitude()))
                        .withZipCodeHidden()
                        .withGooglePlacesEnabled()
                        .withGoogleTimeZoneEnabled()
                        .build(getContext());

                context.startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE);
            }
        });

        holder.direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+values.get(position).getLocation().getLatitude()+","+values.get(position).getLocation().getLongitude()));
                context.startActivity(intent);
            }
        });

        holder.rateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context!=null){
                    RateEventDialog rateEventDiaog= new RateEventDialog(context, values.get(position), new RateEventDialog.DismisListner() {
                        @Override
                        public void onDismiss(float rating) {
                            holder. rateCount.setText(Float.toString(rating));
                            holder.rateImage.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled));
                        }
                    });
                    rateEventDiaog.show();
                }
            }
        });
        View eventDetailsHolder= view.findViewById(R.id.eventDetails);
        SliderView imageSlider= view.findViewById(R.id.imageSlider);
        imageSlider.setSliderAdapter(new SliderAdapter(context, getEventImageStorageReference(values.get(position)), true));
        eventDetailsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.eventDetails.getVisibility()==View.GONE){
                    holder.eventDetails.setVisibility(View.VISIBLE);
                }else{
                    holder.eventDetails.setVisibility(View.GONE);
                }
            }
        });
		return view;
	}





    class ViewHolder {
        TextView eventName;
        TextView name;
        ImageView profileImage;
        ImageView profileImageComment;
        TextView detailsEventName;
        TextView detailsEventDescription;
        TextView detailsEventType;
        TextView detailsStartDate;
        TextView detailsEndDate;
        TextView detailsContactEmailID;
        TextView detailsContactPhoneNumber;
        TextView detailsAddress;
        TextView detailsVenue;
        LinearLayout location;
        LinearLayout direction;
        LinearLayout menuLayout;
        LinearLayout rateEvent;
        LinearLayout participants;
        LinearLayout save;
        ImageView rateImage;
        TextView rateCount;
        TextView viewAllComments;
        TextView viewCount;
        EditText comment;
        CardView eventBox;
        Button postComment;
        View eventDetails;
        TextView saveText;
        TextView participantsCount;
        ImageView saveImage;
    }












    public void reloadData(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("event");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                values.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    values.add(event);
                }
                count=values.size();
                ref.removeEventListener(this);
                notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



	/**
	 * Show more views, or the bottom
	 * @return true if the entire data set is being displayed, false otherwise
	 */
	public boolean showMore(){
		if(count == values.size()) {
			return true;
		}else{
			count = Math.min(count + stepNumber, values.size()); //don't go past the end
			notifyDataSetChanged(); //the count size has changed, so notify the super of the change
			return endReached();
		}
	}

	/**
	 * @return true if then entire data set is being displayed, false otherwise
	 */
	public boolean endReached(){
		return count == values.size();
	}
	
	/**
	 * Sets the ListView back to its initial count number
	 */
	public void reset(){
		count = startCount;
		notifyDataSetChanged();
	}
	static class RetrieveProfileImage extends AsyncTask<Object, Void, Bitmap> {

		private Exception exception;

		protected Bitmap doInBackground(Object... urls) {
			Bitmap image=null;
            Activity activity=null;
			try {
				URL url = new URL(urls[0].toString());
				image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				activity=(Activity) urls[2];
			} catch(IOException e) {
				System.out.println(e);
			}
			final ImageView imageView = (ImageView) urls[1];
			ImageView imageView2 = null;
			if(urls.length>3){
                imageView2= (ImageView) urls[3];
            }
			final Bitmap finalImage = image;
            final Activity finalActivity = activity;
            final ImageView finalImageView = imageView2;
            activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					imageView.setImageDrawable(new BitmapDrawable(finalActivity.getResources(), ImageHelper.getRoundedCornerBitmap(finalImage,2048)));
					if(finalImageView!=null){
                        finalImageView.setImageDrawable(new BitmapDrawable(finalActivity.getResources(), ImageHelper.getRoundedCornerBitmap(finalImage,2048)));
                    }
				}
			});
			return image;
		}

		protected void onPostExecute(Bitmap feed) {
			// TODO: check this.exception
			// TODO: do something with the feed
		}
	}

}
