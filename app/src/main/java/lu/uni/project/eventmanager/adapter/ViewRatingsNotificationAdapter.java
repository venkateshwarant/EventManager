package lu.uni.project.eventmanager.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.pojo.Comment;
import lu.uni.project.eventmanager.pojo.Event;
import lu.uni.project.eventmanager.pojo.RatingNotification;
import lu.uni.project.eventmanager.pojo.User;


public class ViewRatingsNotificationAdapter extends ArrayAdapter<RatingNotification>{

	private FragmentActivity context;
	private List<RatingNotification> values;

	public ViewRatingsNotificationAdapter(FragmentActivity context, List<RatingNotification> values) {
		super(context, R.layout.layout_rating_notification, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_comment, null);
			holder = new ViewHolder();
			holder.profileImage = convertView.findViewById(R.id.profileImage);
			holder.comment =  convertView.findViewById(R.id.comment);
			holder.name =  convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.comment.setText(values.get(position).getUserID());
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		final DatabaseReference userRef = database.getReference("user").child(values.get(position).getUserID());
		userRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				final User user= dataSnapshot.getValue(User.class);
				if(holder.comment.getText().toString().contentEquals(user.getUid())){
					holder.name.setText(user.getDisplayName());
					holder.comment.setText("gave "+values.get(position).getRating()+" rating for your event named "+values.get(position).getEventName());
					EventsAdapter.RetrieveProfileImage task= new EventsAdapter.RetrieveProfileImage();
					task.execute(user.getProfileImgURL(), holder.profileImage, context);
				}
				userRef.removeEventListener(this);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		return convertView;
	}



	@Override
	public RatingNotification getItem(int position){
		return values.get(position);

	}

	@Override
	public int getCount() {
		return values.size();
	}

	class ViewHolder {
		ImageView profileImage;
		TextView comment;
		TextView name;
	}
}
