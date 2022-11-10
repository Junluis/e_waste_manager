package com.capstone.e_waste_manager;

import android.content.Context;
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

import java.util.ArrayList;

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

        HomeModel homeP = homeModelArrayList.get(position);

        holder.author.setText(homeP.homeAuthor);
        holder.title.setText(homeP.homeTitle);
        holder.body.setText(homeP.homeBody);
//        holder.docId = homeP.docId;

    }

    @Override
    public int getItemCount() {
        return homeModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView author, title, body;
        View homeView;

        public MyViewHolder(@NonNull View itemView, HomeInterface homeInterface) {
            super(itemView);

            author = itemView.findViewById(R.id.homeAuthor);
            title = itemView.findViewById(R.id.homeTitle);
            body = itemView.findViewById(R.id.homeBody);
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
