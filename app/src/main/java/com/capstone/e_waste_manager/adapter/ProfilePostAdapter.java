package com.capstone.e_waste_manager.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.e_waste_manager.Fragments.ProfilePostFragment;
import com.capstone.e_waste_manager.Fragments.ProfilePostInterface;
import com.capstone.e_waste_manager.HomeModel;
import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.model.ProfilePostModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

        ProfilePostModel homeP = ProfilePostModel.get(position);

        holder.author.setText(homeP.homeAuthor);
        holder.title.setText(homeP.homeTitle);
        holder.body.setText(homeP.homeBody);
        holder.docId.setText(homeP.docId);
        holder.authorUid.setText(homeP.homeAuthorUid);
        String timeago = calculateTimeAgo(homeP.getHomePostDate().toDate().toString());
        holder.timestamp.setText(timeago);

    }

    private String calculateTimeAgo(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d hh:mm:ss zzz yyyy");
        try {
            long time = sdf.parse(s).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
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
