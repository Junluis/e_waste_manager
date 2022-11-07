package com.capstone.e_waste_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{

    Context context;
    ArrayList<HomeModel> homeModelArrayList;

    public HomeAdapter(Context context, ArrayList<HomeModel> homeModelArrayList) {
        this.context = context;
        this.homeModelArrayList = homeModelArrayList;
    }

    @NonNull
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.home_each, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.MyViewHolder holder, int position) {

        HomeModel homeP = homeModelArrayList.get(position);

        holder.author.setText(homeP.homeAuthor);
        holder.title.setText(homeP.homeTitle);
        holder.body.setText(homeP.homeBody);

    }

    @Override
    public int getItemCount() {
        return homeModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView author, title, body;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.homeAuthor);
            title = itemView.findViewById(R.id.homeTitle);
            body = itemView.findViewById(R.id.homeBody);

        }
    }
}
