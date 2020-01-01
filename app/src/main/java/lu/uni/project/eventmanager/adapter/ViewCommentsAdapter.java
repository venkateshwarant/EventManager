package lu.uni.project.eventmanager.adapter;


import android.opengl.Visibility;

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


public class ViewCommentsAdapter extends ArrayAdapter<Comment>{

	private FragmentActivity context;
	private List<Comment> values;


	public ViewCommentsAdapter(FragmentActivity context, List<Comment> values) {
		super(context, R.layout.layout_comment, values);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_comment, null);
			holder = new ViewHolder();
			holder.commenHolder= convertView.findViewById(R.id.commentHolder);
			holder.commentOptions= convertView.findViewById(R.id.commentOptions);
			holder.profileImage = convertView.findViewById(R.id.profileImage);
			holder.comment =  convertView.findViewById(R.id.comment);
			holder.editCommentBox= convertView.findViewById(R.id.editCommentBox);
			holder.name =  convertView.findViewById(R.id.name);
			holder.edit =  convertView.findViewById(R.id.editComment);
			holder.editBox =  convertView.findViewById(R.id.editBox);
			holder.postEditComment =  convertView.findViewById(R.id.postEditComment);
			holder.delete =  convertView.findViewById(R.id.deleteComment);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.comment.setText(values.get(position).getComment());
		holder.commenHolder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(holder.commentOptions.getVisibility()== View.GONE){
					holder.commentOptions.setVisibility(View.VISIBLE);
				}else{
					holder.commentOptions.setVisibility(View.GONE);
				}
			}
		});
		holder.edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.editBox.setVisibility(View.VISIBLE);
				holder.comment.setVisibility(View.GONE);
				holder.editCommentBox.setText(values.get(position).getComment());
				holder.postEditComment.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final FirebaseDatabase database = FirebaseDatabase.getInstance();
						DatabaseReference ref = database.getReference("comment").child(values.get(position).getEventID()).child(values.get(position).getCommentID());
						Comment newComment= values.get(position);
						newComment.setComment(holder.editCommentBox.getText().toString());
						ref.setValue(newComment);
						holder.editBox.setVisibility(View.GONE);
						holder.comment.setText(holder.editCommentBox.getText().toString());
						holder.comment.setVisibility(View.VISIBLE);
						Toast.makeText(getContext(),"Comment Edited!",Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		holder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final FirebaseDatabase database = FirebaseDatabase.getInstance();
				DatabaseReference ref = database.getReference("comment").child(values.get(position).getEventID()).child(values.get(position).getCommentID());
				ref.removeValue(new DatabaseReference.CompletionListener() {
					@Override
					public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
						values.remove(position);
						notifyDataSetChanged();
						Toast.makeText(getContext(),"Comment deleted!",Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		final String comm=values.get(position).getComment();
		final String uid=values.get(position).getUserID();
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		final DatabaseReference userRef = database.getReference("user").child(values.get(position).getUserID());
		userRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User user= dataSnapshot.getValue(User.class);
				if(user!=null& uid.contentEquals(user.getUid()) & holder.comment.getText().toString().contentEquals(values.get(position).getComment())){
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
	public Comment getItem(int position){
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
		EditText editCommentBox;
		LinearLayout editBox;
		LinearLayout commentOptions;
		LinearLayout postEditComment;
		LinearLayout commenHolder;
		LinearLayout edit;
		LinearLayout delete;
	}
}
