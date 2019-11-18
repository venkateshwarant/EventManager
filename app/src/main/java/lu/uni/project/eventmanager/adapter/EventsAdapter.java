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
import lu.uni.project.eventmanager.bottomsheet.ViewAllCommentsBottomSheetFragment;
import lu.uni.project.eventmanager.dialog.RateEventDialog;
import lu.uni.project.eventmanager.pojo.Comment;
import lu.uni.project.eventmanager.pojo.Event;
import lu.uni.project.eventmanager.pojo.User;
import lu.uni.project.eventmanager.util.ImageHelper;


public class EventsAdapter extends ArrayAdapter<Event>{

	private FragmentActivity context;
	private List<Event> values;
	private int count;
	private int stepNumber;
	private int startCount;
	private int MAP_BUTTON_REQUEST_CODE= 1;

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

	public static void getEventsASynchronously(ValueEventListener listner)  {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("event");
		ref.addValueEventListener(listner);
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

    public List<String> getEventComments(Event event){
        final List<String> stringList= new ArrayList<>();

        return stringList;
    }
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		if(convertView == null){
			view = LayoutInflater.from(context).inflate(R.layout.row_layout, null);
            TextView eventName= view.findViewById(R.id.eventName);
            final TextView name= view.findViewById(R.id.name);
            final ImageView profileImage= view.findViewById(R.id.profile_image);
            TextView detailsEventName= view.findViewById(R.id.detailsEventName);
            TextView detailsEventDescription= view.findViewById(R.id.detailsEventDescrition);
            TextView detailsEventType= view.findViewById(R.id.detailsEventType);
            TextView detailsStartDate= view.findViewById(R.id.detailsStartDate);
            TextView detailsEndDate= view.findViewById(R.id.detailsEndDate);
            TextView detailsContactEmailID= view.findViewById(R.id.detailsContactEmail);
            TextView detailsContactPhoneNumber= view.findViewById(R.id.detailscontactPhNo);
            TextView detailsAddress= view.findViewById(R.id.detailsLocationAddress);
            TextView detailsVenue= view.findViewById(R.id.detailsVenueDetails);
            LinearLayout location= view.findViewById(R.id.location);
            LinearLayout direction= view.findViewById(R.id.direction);
            final LinearLayout menuLayout= view.findViewById(R.id.eventMenuIcon);
            final LinearLayout rateEvent= view.findViewById(R.id.rate);
            final ImageView rateImage= view.findViewById(R.id.starImage);
            final TextView rateCount= view.findViewById(R.id.rateCount);
            TextView viewAllComments= view.findViewById(R.id.viewAllComments);
            final EditText comment= view.findViewById(R.id.commentBox);
            final CardView eventBox= view.findViewById(R.id.eventBox);
            Button postComment= view.findViewById(R.id.commentBtn);

            eventName.setText(values.get(position).getEventName());
            detailsEventName.setText(values.get(position).getEventName());
            detailsEventDescription.setText(values.get(position).getEventDescription());
            detailsEventType.setText(values.get(position).getEventCategory());
            detailsStartDate.setText(values.get(position).getStartDate());
            detailsEndDate.setText(values.get(position).getEndDate());
            detailsContactEmailID.setText("");
            detailsContactPhoneNumber.setText("");
            detailsAddress.setText(values.get(position).getLocation().getAddress());
            detailsVenue.setText(values.get(position).getLocation().getVenueDetails());

			final FirebaseDatabase database = FirebaseDatabase.getInstance();
			FirebaseAuth auth = FirebaseAuth.getInstance();
			FirebaseUser user = auth.getCurrentUser();
			String uid = "";
			if (user != null) {
				uid = user.getUid();
			}
			final DatabaseReference userRef = database.getReference("user").child(values.get(position).getUserId());
			userRef.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					final User user= dataSnapshot.getValue(User.class);
					if(user!=null){
						name.setText(user.getDisplayName());
						RetrieveProfileImage task= new RetrieveProfileImage();
						task.execute(user.getProfileImgURL(), profileImage, context);
						menuLayout.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if(user.getUid()!=null&& FirebaseAuth.getInstance().getCurrentUser()!=null){
									PopupMenu popupMenu = new PopupMenu(context, menuLayout);
									if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
										popupMenu.getMenu().add("Delete event");
										popupMenu.getMenu().add("Edit event");
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
                                                                FirebaseDatabase.getInstance().getReference("event").child(values.get(position).getEventId()).removeValue();
                                                                FirebaseDatabase.getInstance().getReference("rating").child(values.get(position).getEventId()).removeValue();
                                                                FirebaseDatabase.getInstance().getReference("comment").child(values.get(position).getEventId()).removeValue();
                                                                for(int i=0;i<values.get(position).imagesCount;i++){
                                                                    FirebaseStorage.getInstance().getReference().child("images").child(values.get(position).getEventId()).child(i+".jpg").delete();
                                                                }
                                                                eventBox.setVisibility(View.GONE);
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
			final DatabaseReference ref = database.getReference("rating").child(values.get(position).getEventId()).child(uid);
			ref.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					if(dataSnapshot.getValue()!=null){
						rateCount.setText(dataSnapshot.getValue().toString());
						rateImage.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled));
					}
					ref.removeEventListener(this);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {

				}
			});

            viewAllComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllCommentsBottomSheetFragment btmSheet= new ViewAllCommentsBottomSheetFragment(values.get(position));
                    btmSheet.show(context.getSupportFragmentManager(), "");
                }
            });

            postComment.setOnClickListener(new View.OnClickListener() {
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
                    commentObj.setComment(comment.getText().toString());
                    commentObj.setCreatedTime(Long.toString(System.currentTimeMillis()));
                    commentObj.setUserID(uid);
                    commentObj.setCommentID(db.push().getKey());
                    db.child(values.get(position).getEventId()).child(commentObj.getCommentID()).setValue(commentObj);
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    comment.setText("");
                    Toast.makeText(context,"Comment added!", Toast.LENGTH_SHORT).show();
                }
            });
            location.setOnClickListener(new View.OnClickListener() {
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

            direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="+values.get(position).getLocation().getLatitude()+","+values.get(position).getLocation().getLongitude()));
                    context.startActivity(intent);
                }
            });

			rateEvent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(context!=null){
						RateEventDialog rateEventDiaog= new RateEventDialog(context, values.get(position), new RateEventDialog.DismisListner() {
							@Override
							public void onDismiss(float rating) {
                                rateCount.setText(Float.toString(rating));
                                rateImage.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled));
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
					View eventDetails= view.findViewById(R.id.eventDetailsScrollPort);
					if(eventDetails.getVisibility()==View.GONE){
						eventDetails.setVisibility(View.VISIBLE);
					}else{
						eventDetails.setVisibility(View.GONE);
					}
				}
			});
		}else{
			view = convertView;
		}
		return view;
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
			final Bitmap finalImage = image;
            final Activity finalActivity = activity;
            activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					imageView.setImageDrawable(new BitmapDrawable(finalActivity.getResources(), ImageHelper.getRoundedCornerBitmap(finalImage,2048)));
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
