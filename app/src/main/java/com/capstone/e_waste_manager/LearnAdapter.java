package com.capstone.e_waste_manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.MyViewHolder>{

    LearnInterface learnInterface;

    Context context;
    ArrayList<LearnModel> learnModelArrayList;

    public LearnAdapter(Context context, ArrayList<LearnModel> learnModelArrayList, LearnInterface learnInterface) {
        this.context = context;
        this.learnModelArrayList = learnModelArrayList;
        this.learnInterface = learnInterface;
    }

    @NonNull
    @Override
    public LearnAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.learn_each, parent, false);

        return new MyViewHolder(v, learnInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        LearnModel learnP = learnModelArrayList.get(position);

        holder.author.setText(learnP.learnAuthor);
        holder.title.setText(learnP.learnTitle);
        holder.body.setText(learnP.learnBody);
        Picasso.get().load(learnP.learnImage).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return learnModelArrayList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView author, title, body;
        ImageView image;
        View homeView;

        public MyViewHolder(@NonNull View itemView, LearnInterface learnInterface){
            super(itemView);

            author = itemView.findViewById(R.id.learnAuthor);
            title = itemView.findViewById(R.id.learnTitle);
            body = itemView.findViewById(R.id.learnBody);
            image = itemView.findViewById(R.id.learnImage);
            homeView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (learnInterface != null ){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            learnInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
