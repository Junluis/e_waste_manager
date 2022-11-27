package com.capstone.e_waste_manager.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.CommentModel;
import com.capstone.e_waste_manager.HomeModel;
import com.capstone.e_waste_manager.HomeView;
import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.model.ReplyModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class RepliesToComment extends BottomSheetDialogFragment {

    private boolean isTouch = false;
    boolean identifier = false;
    BottomSheetDialog dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return dialog;
    }

    public RepliesToComment() {
        // Required empty public constructor
    }

    ImageButton bckBtn, more;
    ImageView prof_img;
    TextView commentAuthor, timestamp, commentAuthorUid, docId, commentBody, upvotecount, downvotecount;
    ToggleButton upvotecomment, downvotecomment, replypop;
    TextInputLayout tilpReply;
    Chip replyChip;
    Button replyBtn;
    EditText pReply;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    String userID;
    FirebaseUser user;

    //recycler
    RecyclerView replyRecycler;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replies_to_comment, container, false);

        //comment details
        bckBtn = view.findViewById(R.id.bckBtn);
        commentAuthor = view.findViewById(R.id.commentAuthor);
        timestamp = view.findViewById(R.id.timestamp);
        commentAuthorUid = view.findViewById(R.id.commentAuthorUid);
        docId = view.findViewById(R.id.docId);
        commentBody = view.findViewById(R.id.commentBody);
        prof_img = view.findViewById(R.id.prof_img);

        //reply to commnet
        replypop = view.findViewById(R.id.replypop);
        replyChip = view.findViewById(R.id.replyChip);
        tilpReply = view.findViewById(R.id.tilpReply);
        replyBtn = view.findViewById(R.id.replyBtn);
        pReply = view.findViewById(R.id.pReply);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();
        userID = user.getUid();


        //model and comment details
        assert getArguments() != null;
        CommentModel model = (CommentModel) getArguments().getSerializable("model");

        commentAuthor.setText(model.getCommentAuthor());
        TimeAgo2 timeAgo2 = new TimeAgo2();
        String timeago = timeAgo2.covertTimeToText(model.getCommentPostDate().toString());
        timestamp.setText(timeago);
        commentAuthorUid.setText(model.getCommentUid());
        docId.setText(model.getDocId());
        commentBody.setText(model.getCommentBody());

        StorageReference profileRef = storageReference.child("ProfileImage/"+commentAuthorUid.getText().toString()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(prof_img);
            }
        });

        //reply btn
        replyChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tilpReply.setVisibility(View.GONE);
                replyBtn.setVisibility(View.GONE);
                replyChip.setVisibility(View.GONE);
                replypop.setChecked(false);
                pReply.setText("");
            }
        });

        replypop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    tilpReply.setVisibility(View.VISIBLE);
                    replyBtn.setVisibility(View.VISIBLE);
                    replyChip.setVisibility(View.VISIBLE);
                    replypop.setVisibility(View.GONE);
                    replyChip.setText(commentAuthor.getText());
                }else{
                    tilpReply.setVisibility(View.GONE);
                    replyBtn.setVisibility(View.GONE);
                    replyChip.setVisibility(View.GONE);
                    replypop.setVisibility(View.VISIBLE);
                }
            }
        });

        //back btn
        bckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //comment recycler
        replyRecycler = view.findViewById(R.id.replyRecycler);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        replyRecycler.setLayoutManager(linearLayoutManager);

        replyRecycler.setItemAnimator(null);


//        Query query = fStore.collection("Post")
//                .document(pdocId.getText().toString()).collection("comment")
//                .orderBy("commentPostDate", Query.Direction.DESCENDING)
//                .limit(50);
//
//        FirestoreRecyclerOptions<CommentModel> options = new FirestoreRecyclerOptions.Builder<CommentModel>()
//                .setQuery(query, CommentModel.class)
//                .build();
//
//        adapter2 = new FirestoreRecyclerAdapter<CommentModel, HomeView.ViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull HomeView.ViewHolder holder, int position, @NonNull CommentModel model) {
//                holder.bind(model);
//            }
//
//            @NonNull
//            @Override
//            public HomeView.ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
//                View view = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.comment_each, group,false);
//                return new HomeView.ViewHolder(view);
//            }
//        };
//
//        commentRecycler.setAdapter(adapter2);
//
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onRefresh() {
//                commentRecycler.setAdapter(null);
//                commentRecycler.setAdapter(adapter2);
//                adapter2.startListening();
//                adapter2.notifyDataSetChanged();
//                votecounter();
//                swipeRefresh.setRefreshing(false);
//            }
//        });

        return view;
    }
}