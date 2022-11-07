package com.capstone.e_waste_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.MyViewHolder> {

    Context context;
    ArrayList<LearnModel> learnModelArrayList;

    public LearnAdapter(Context context, ArrayList<LearnModel> learnModelArrayList) {
        this.context = context;
        this.learnModelArrayList = learnModelArrayList;
    }

    @NonNull
    @Override
    public LearnAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.learn_each, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LearnAdapter.MyViewHolder holder, int position) {

        LearnModel learnP = learnModelArrayList.get(position);

        holder.author.setText(learnP.learnAuthor);
        holder.title.setText(learnP.learnTitle);
        holder.body.setText(learnP.learnBody);

    }

    @Override
    public int getItemCount() {
        return learnModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView author, title, body;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            author = itemView.findViewById(R.id.learnAuthor);
            title = itemView.findViewById(R.id.learnTitle);
            body = itemView.findViewById(R.id.learnBody);
        }
    }
}
