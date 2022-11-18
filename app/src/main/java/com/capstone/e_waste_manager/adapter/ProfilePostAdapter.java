package com.capstone.e_waste_manager.adapter;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.e_waste_manager.Fragments.ProfilePostFragment;
import com.capstone.e_waste_manager.Fragments.ProfilePostInterface;
import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.model.ProfilePostModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.MyViewHolder>{
    ProfilePostInterface postInterface;

    Context context;
    ArrayList<ProfilePostModel> ProfilePostModel;

    public ProfilePostAdapter(Context context, ArrayList<ProfilePostModel> postModelArrayList, ProfilePostFragment postInterface) {
        this.context = context;
        this.ProfilePostModel = postModelArrayList;
        this.postInterface = (ProfilePostInterface) postInterface;
    }

    @NonNull
    @Override
    public ProfilePostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.home_each, parent, false);

        return new MyViewHolder(v, postInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostAdapter.MyViewHolder holder, int position) {

        DocumentSnapshot.ServerTimestampBehavior behavior = ESTIMATE;
        ProfilePostModel homeP = ProfilePostModel.get(position);

        if (homeP.getHomePostDate() != null) {
            holder.author.setText(homeP.homeAuthor);
            holder.title.setText(homeP.homeTitle);
            holder.body.setText(homeP.homeBody);
            holder.docId.setText(homeP.docId);
            holder.authorUid.setText(homeP.homeAuthorUid);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            String timeago = timeAgo2.covertTimeToText(homeP.getHomePostDate().toDate().toString());
            holder.timestamp.setText(timeago);
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
    public int getItemCount() { return ProfilePostModel.size(); }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView author, title, body, authorUid, docId, timestamp;
        View homeView;

        public MyViewHolder(@NonNull View itemView, ProfilePostInterface postInterface) {
            super(itemView);

            author = itemView.findViewById(R.id.homeAuthor);
            title = itemView.findViewById(R.id.homeTitle);
            body = itemView.findViewById(R.id.homeBody);
            docId = itemView.findViewById(R.id.docId);
            authorUid = itemView.findViewById(R.id.homeAuthorUid);
            timestamp = itemView.findViewById(R.id.timestamp);
            homeView = itemView;


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            postInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
