package com.capstone.e_waste_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
        holder.time.setText(commentP.commentTime);
    }

    @Override
    public int getItemCount() {
        return commentModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView author, body, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.commentAuthor);
            body = itemView.findViewById(R.id.commentBody);
            time = itemView.findViewById(R.id.commentTime);

        }
    }
}
