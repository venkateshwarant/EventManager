package lu.uni.project.eventmanager.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.pojo.Comment;
import lu.uni.project.eventmanager.pojo.Event;
import lu.uni.project.eventmanager.pojo.User;


public class ParticiantsAdapter extends ArrayAdapter<String>{

	private FragmentActivity context;
	private List<String> values;


	public ParticiantsAdapter(FragmentActivity context, List<String> values) {
		super(context, R.layout.layout_participant, values);
		this.context = context;
		this.values = values;
	}

	public static void getCommentsForEventASynchronously(ValueEventListener listner, Event event)  {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("comment").child(event.getEventId());
		ref.addValueEventListener(listner);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_participant, null);
			holder = new ViewHolder();
			holder.profileImage = convertView.findViewById(R.id.profileImage);
			holder.name =  convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		final DatabaseReference userRef = database.getReference("user").child(values.get(position));
		userRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User user= dataSnapshot.getValue(User.class);
				if(user!=null){
					holder.name.setText(user.getDisplayName());
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
	public String getItem(int position){
		return values.get(position);

	}

	@Override
	public int getCount() {
		return values.size();
	}

	class ViewHolder {
		ImageView profileImage;
		TextView name;
	}
}
