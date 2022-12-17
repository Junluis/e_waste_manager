package com.capstone.e_waste_manager;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.MyViewHolder> {

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

        StorageReference coverRef = holder.storageReference.child("LearningMaterial/"+learnP.docId+"/coverimg.jpg");
        coverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

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
        ImageView image;


        //firebase
        FirebaseFirestore fStore;
        StorageReference storageReference;
        FirebaseAuth fAuth;
        FirebaseUser user;

        public MyViewHolder(@NonNull View itemView, LearnInterface learnInterface){
            super(itemView);

            author = itemView.findViewById(R.id.learnAuthor);
            title = itemView.findViewById(R.id.learnTitle);
            body = itemView.findViewById(R.id.learnBody);
            image = itemView.findViewById(R.id.learnImage);

            //firebase
            fStore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            fAuth = FirebaseAuth.getInstance();
            user = fAuth.getCurrentUser();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(learnInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                         learnInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
