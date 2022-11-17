package com.capstone.e_waste_manager;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.e_waste_manager.adapter.ProfilePostAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    Context context;
    ArrayList<CommentModel> commentModelArrayList;

    public CommentAdapter(Context context, ArrayList<CommentModel> commentModelArrayList){
        this.context = context;
        this.commentModelArrayList = commentModelArrayList;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.comment_each, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {

        CommentModel commentP = commentModelArrayList.get(position);

        holder.author.setText(commentP.commentAuthor);
        holder.body.setText(commentP.commentBody);
        holder.authorUid.setText(commentP.commentUid);
        holder.docId.setText(commentP.docId);
        if (!(commentP.getCommentPostDate() == null)){
            TimeAgo2 timeAgo2 = new TimeAgo2();
            String timeago = timeAgo2.covertTimeToText(commentP.getCommentPostDate().toDate().toString());
            holder.time.setText(timeago);
        }else {
            holder.time.setText("0 second ago");
        }
    }


    public class TimeAgo2 {

        public String covertTimeToText(String dataDate) {

            String convTime = null;

            String prefix = "";
            String suffix = "Ago";

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss zzz yyyy");
                Date pasTime = dateFormat.parse(dataDate);

                Date nowTime = new Date();

                long dateDiff = nowTime.getTime() - pasTime.getTime();

                long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
                long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
                long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
                long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

                if (second == 0) {
                    convTime = second + " Second " + suffix;
                }else if (second < 60) {
                    convTime = second + " Seconds " + suffix;
                } else if (minute < 60) {
                    convTime = minute + " Minutes "+suffix;
                } else if (hour < 24) {
                    convTime = hour + " Hours "+suffix;
                } else if (day >= 7) {
                    if (day > 360) {
                        convTime = (day / 360) + " Years " + suffix;
                    } else if (day > 30) {
                        convTime = (day / 30) + " Months " + suffix;
                    } else {
                        convTime = (day / 7) + " Week " + suffix;
                    }
                } else if (day < 7) {
                    convTime = day+" Days "+suffix;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return convTime;
        }

    }

    @Override
    public int getItemCount() {
        return commentModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView author, body, time, authorUid, docId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.commentAuthor);
            body = itemView.findViewById(R.id.commentBody);
            time = itemView.findViewById(R.id.timestamp);
            authorUid = itemView.findViewById(R.id.commentAuthorUid);
            docId = itemView.findViewById(R.id.docId);
        }
    }
}
