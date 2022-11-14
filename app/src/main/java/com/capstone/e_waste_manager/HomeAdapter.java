package com.capstone.e_waste_manager;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.core.ViewSnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{
    HomeInterface homeInterface;

    Context context;
    ArrayList<HomeModel> homeModelArrayList;

    public HomeAdapter(Context context, ArrayList<HomeModel> homeModelArrayList, HomeInterface homeInterface) {
        this.context = context;
        this.homeModelArrayList = homeModelArrayList;
        this.homeInterface = homeInterface;
    }

    @NonNull
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.home_each, parent, false);

        return new MyViewHolder(v, homeInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.MyViewHolder holder, int position) {

        DocumentSnapshot.ServerTimestampBehavior behavior = ESTIMATE;
        HomeModel homeP = homeModelArrayList.get(position);

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
    public int getItemCount() { return homeModelArrayList.size(); }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView author, title, body, authorUid, docId, timestamp;
        View homeView;

        public MyViewHolder(@NonNull View itemView, HomeInterface homeInterface) {
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
                    if (homeInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            homeInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
