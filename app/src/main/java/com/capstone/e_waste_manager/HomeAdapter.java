package com.capstone.e_waste_manager;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.core.ViewSnapshot;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> implements Filterable {
    HomeInterface homeInterface;

    Context context;
    ArrayList<HomeModel> homeModelArrayList;
    ArrayList<HomeModel> homeSearchList;

    public HomeAdapter(Context context, ArrayList<HomeModel> homeModelArrayList, HomeInterface homeInterface) {
        this.context = context;
        this.homeModelArrayList = homeModelArrayList;
        this.homeSearchList = new ArrayList<>(homeModelArrayList);
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

        HomeModel homeP = homeModelArrayList.get(position);

        holder.author.setText(homeP.homeAuthor);
        holder.title.setText(homeP.homeTitle);
        holder.body.setText(homeP.homeBody);
        holder.docId.setText(homeP.docId);
        holder.authorUid.setText(homeP.homeAuthorUid);
        String timeago = calculateTimeAgo(homeP.homePostDate.toDate().toString());
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

    @Override
    public Filter getFilter() {
        return postFilter;
    }

    public final Filter postFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<HomeModel> filteredHomeList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredHomeList.addAll(homeModelArrayList);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (HomeModel homeModel : homeModelArrayList){
                    if(homeModel.homeTitle.toLowerCase().contains(filterPattern))
                        filteredHomeList.add(homeModel);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredHomeList;
            results.count = filteredHomeList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            homeSearchList.clear();
            homeSearchList.addAll((ArrayList)results.values);
            notifyDataSetChanged();

        }
    };

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
