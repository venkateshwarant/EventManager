package lu.uni.project.eventmanager.bottomsheet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import lu.uni.project.eventmanager.R
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import lu.uni.project.eventmanager.adapter.ViewCommentsAdapter
import lu.uni.project.eventmanager.pojo.Comment
import lu.uni.project.eventmanager.pojo.Event

class ViewAllCommentsBottomSheetFragment(val event:Event): SuperBottomSheetFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        var root= inflater.inflate(R.layout.layout_view_all_comments, container, false)
        var commentListView = root.findViewById(R.id.commentList) as ListView
        var adapter: ViewCommentsAdapter?
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("comment").child(event.getEventId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outputList = mutableListOf<Comment>()
                for (postSnapshot in dataSnapshot.children) {
                    val commentObj = postSnapshot.getValue(Comment::class.java)
                    outputList.add(commentObj!!)
                }
                adapter = ViewCommentsAdapter(activity, outputList)
                commentListView.adapter = adapter
                ref.removeEventListener(this)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        return root
    }

    override fun getCornerRadius() = context!!.resources.getDimension(R.dimen.btm_sht_crnr_radius)

    override fun getStatusBarColor() = Color.BLACK
}