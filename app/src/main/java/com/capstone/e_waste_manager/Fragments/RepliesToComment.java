package com.capstone.e_waste_manager.Fragments;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.CommentModel;
import com.capstone.e_waste_manager.HomeView;
import com.capstone.e_waste_manager.Login;
import com.capstone.e_waste_manager.NoConnection;
import com.capstone.e_waste_manager.Post;
import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.Register;
import com.capstone.e_waste_manager.model.ReplyModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import soup.neumorphism.NeumorphFloatingActionButton;

public class RepliesToComment extends BottomSheetDialogFragment {
    boolean identifier = false;
    BottomSheetDialog dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        return dialog;
    }

    public void expand() {
        if (!identifier) {
            dialog.getBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        dialog.getBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState != BottomSheetBehavior.STATE_COLLAPSED) {
                    expandBtn.setChecked(true);
                } else {
                    expandBtn.setChecked(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

    }


    public RepliesToComment() {
        // Required empty public constructor
    }

    ImageButton bckBtn, more;
    ImageView prof_img;
    TextView commentAuthor, timestamp, commentAuthorUid, docId, commentBody, upvotecount, downvotecount;
    ToggleButton upvotecomment, downvotecomment, replypop, expandBtn;
    TextInputLayout tilpReply;
    Chip replyChip, replyChipView;
    Button replyBtn;
    EditText pReply;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    String userID;
    FirebaseUser user;
    FirestoreRecyclerAdapter adapter3;

    //recycler
    RecyclerView replyRecycler;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefresh;

    ScrollView scrollView3;

    Dialog guestDialog;
    AlertDialog alrtdialog;

    String CommentOrigin;

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

        //reply to comment
        replypop = view.findViewById(R.id.replypop);
        replyChip = view.findViewById(R.id.replyChip);
        replyChipView = view.findViewById(R.id.replyChipView);
        tilpReply = view.findViewById(R.id.tilpReply);
        replyBtn = view.findViewById(R.id.replyBtn);
        pReply = view.findViewById(R.id.pReply);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();
        userID = user.getUid();

        scrollView3 = view.findViewById(R.id.scrollView3);

        expandBtn = view.findViewById(R.id.expandBtn);

        //vote
        upvotecomment = view.findViewById(R.id.upvotecomment);
        downvotecomment = view.findViewById(R.id.downvotecomment);
        upvotecount = view.findViewById(R.id.upvotecount);
        downvotecount = view.findViewById(R.id.downvotecount);

        //Guest dialog
        guestDialog = new Dialog(getActivity());

        //model and comment details
        assert getArguments() != null;
        CommentModel model = (CommentModel) getArguments().getSerializable("model");

        TimeAgo2 timeAgo2 = new TimeAgo2();
        String timeago = timeAgo2.covertTimeToText(model.getCommentPostDate().toString());
        timestamp.setText(timeago);
        commentAuthorUid.setText(model.getCommentUid());
        docId.setText(model.getDocId());
        commentBody.setText(model.getCommentBody());
        DocumentReference usernameReference = fStore.collection("Users").document(model.getCommentUid());
        usernameReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                commentAuthor.setText(documentSnapShot.getString("Username"));
            }
        });

        StorageReference profileRef = storageReference.child("ProfileImage/" + commentAuthorUid.getText().toString() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(prof_img);
            }
        });
        CommentOrigin = model.getCommentPostOrigin().toString();


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
                if (user != null && !user.isAnonymous()) {
                    if (b) {
                        tilpReply.setVisibility(View.VISIBLE);
                        replyBtn.setVisibility(View.VISIBLE);
                        replyChip.setVisibility(View.GONE);
                        replypop.setVisibility(View.GONE);
                        replyChip.setText(commentAuthor.getText());
                        replyChipView.setText(commentAuthor.getText());
                    } else {
                        tilpReply.setVisibility(View.GONE);
                        replyBtn.setVisibility(View.GONE);
                        replyChipView.setVisibility(View.GONE);
                        replypop.setVisibility(View.VISIBLE);
                    }
                } else {
                    ShowPopup();
                    replypop.setChecked(false);
                }
            }
        });

        //reply to comment
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                if (user != null && !user.isAnonymous()) {
                    DocumentReference documentReference = fStore.collection("Post").document(model.getCommentPostOrigin())
                            .collection("comment").document(model.getDocId());
                    fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String username = task.getResult().getString("Username");
                            Map<String, Object> comment = new HashMap<>();
                            comment.put("replyAuthorUid", userID);
                            comment.put("replyAuthor", username);
                            comment.put("replyBody", pReply.getText().toString());
                            comment.put("replyChip", replyChipView.getText().toString());
                            comment.put("replyPostDate", FieldValue.serverTimestamp());
                            comment.put("replyPostOrigin", model.getDocId());

                            documentReference.collection("reply").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    replyChip.performCloseIconClick();
                                    pReply.setText("");
                                    adapter3.notifyDataSetChanged();
                                    hideProgressDialog();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pReply.setText("");
                                    showEmptyView();
                                    adapter3.notifyDataSetChanged();
                                    hideProgressDialog();
                                }
                            });
                            hideProgressDialog();
                        }
                    });
                } else {
                    ShowPopup();
                    hideProgressDialog();
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

        //reply recycler
        replyRecycler = view.findViewById(R.id.replyRecycler);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        replyRecycler.setLayoutManager(linearLayoutManager);

        replyRecycler.setItemAnimator(null);

        Query query = fStore.collection("Post")
                .document(model.getCommentPostOrigin()).collection("comment")
                .document(model.getDocId()).collection("reply")
                .orderBy("replyPostDate", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ReplyModel> options = new FirestoreRecyclerOptions.Builder<ReplyModel>()
                .setQuery(query, ReplyModel.class)
                .build();

        adapter3 = new FirestoreRecyclerAdapter<ReplyModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ReplyModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.reply_each, group, false);
                return new ViewHolder(view);
            }
        };

        replyRecycler.setAdapter(adapter3);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                replyRecycler.setAdapter(null);
                replyRecycler.setAdapter(adapter3);
                adapter3.startListening();
                adapter3.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        expand();
        //expand
        expandBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                identifier = b;
                if (b) {
                    expandBtn.animate().rotation(90f).setDuration(300);
                } else {
                    expandBtn.animate().rotation(-90f).setDuration(300);
                }
                expand();
            }
        });
        scrollView3.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int bottom = (scrollView3.getChildAt(scrollView3.getChildCount() - 1)).getHeight() - scrollView3.getHeight() - scrollView3.getScrollY();
                if (bottom == 0) {
                    expandBtn.setChecked(true);
                    expand();
                }
            }
        });

        if (user != null && !user.isAnonymous()) {
            DocumentReference documentReference = fStore.collection("Post").document(model.getCommentPostOrigin())
                    .collection("comment").document(model.getDocId()).collection("vote")
                    .document(fAuth.getCurrentUser().getUid());
            documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Upvote"))) {
                        upvotecomment.setChecked(true);
                        downvotecomment.setChecked(false);
                    } else if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Downvote"))) {
                        downvotecomment.setChecked(true);
                        upvotecomment.setChecked(false);
                    } else {
                        upvotecomment.setChecked(false);
                        downvotecomment.setChecked(false);
                    }
                }
            });
        }
        if (user != null && !user.isAnonymous()) {
            upvotecomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (upvotecomment.isChecked()) {
                        Map<String, Object> vote = new HashMap<>();
                        vote.put("Upvote", true);

                        fStore.collection("Post").document(model.getCommentPostOrigin())
                                .collection("comment").document(model.getDocId()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
                                    }
                                });
                    } else if (!upvotecomment.isChecked() && !downvotecomment.isChecked()) {
                        fStore.collection("Post").document(model.getCommentPostOrigin())
                                .collection("comment").document(model.getDocId()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
                                    }
                                });
                    }
                }
            });
            downvotecomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (downvotecomment.isChecked()) {
                        Map<String, Object> vote = new HashMap<>();
                        vote.put("Downvote", true);

                        fStore.collection("Post").document(model.getCommentPostOrigin())
                                .collection("comment").document(model.getDocId()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
                                    }
                                });
                    } else if (!upvotecomment.isChecked() && !downvotecomment.isChecked()) {
                        fStore.collection("Post").document(model.getCommentPostOrigin())
                                .collection("comment").document(model.getDocId()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
                                    }
                                });
                    }
                }
            });
        } else {
            upvotecomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPopup();
                    upvotecomment.setChecked(false);
                    downvotecomment.setChecked(false);
                }
            });
            downvotecomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPopup();
                    upvotecomment.setChecked(false);
                    downvotecomment.setChecked(false);
                }
            });
        }

        return view;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView replyAuthor, timestamp, replyAuthorUid, docId, replyBody, upvotereplycount, downvotereplycount;
        ImageView prof_img;
        ImageButton more;
        ToggleButton replyupvote, replydownvote, replybtn;
        Chip Chip;

        ReplyModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //details
            replyAuthor = itemView.findViewById(R.id.replyAuthor);
            timestamp = itemView.findViewById(R.id.timestamp);
            replyAuthorUid = itemView.findViewById(R.id.replyAuthorUid);
            docId = itemView.findViewById(R.id.docId);
            replyBody = itemView.findViewById(R.id.replyBody);
            prof_img = itemView.findViewById(R.id.prof_img);

            //votes
            upvotereplycount = itemView.findViewById(R.id.upvotecount);
            downvotereplycount = itemView.findViewById(R.id.downvotecount);
            replyupvote = itemView.findViewById(R.id.upvote);
            replydownvote = itemView.findViewById(R.id.downvote);

            //button
            more = itemView.findViewById(R.id.more);
            replybtn = itemView.findViewById(R.id.replypop);
            Chip = itemView.findViewById(R.id.replyChip);

        }

        public void bind(ReplyModel replyModel) {
            model = replyModel;
            replyBody.setText(replyModel.replyBody);
            docId.setText(replyModel.replydocId);
            replyAuthorUid.setText(replyModel.replyAuthorUid);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            if (replyModel.getReplyPostDate() != null) {
                String timeago = timeAgo2.covertTimeToText(replyModel.replyPostDate.toString());
                timestamp.setText(timeago);
            }
            if (!replyModel.replyChip.contains("@")) {
                Chip.setVisibility(View.GONE);
            } else {
                DocumentReference chip = fStore.collection("Users").document(replyModel.replyChip.replace("@", ""));
                chip.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                        Chip.setText("@" + documentSnapShot.getString("Username"));
                    }
                });
            }
            DocumentReference usernameReference = fStore.collection("Users").document(replyModel.replyAuthorUid);
            usernameReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    replyAuthor.setText(documentSnapShot.getString("Username"));
                }
            });

            //vote counter
            CollectionReference vote = fStore.collection("Post").document(CommentOrigin)
                    .collection("comment").document(replyModel.replyPostOrigin)
                    .collection("reply").document(replyModel.replydocId)
                    .collection("vote");
            AggregateQuery Upvotecount = vote.whereEqualTo("Upvote", true).count();
            AggregateQuery Downvotecount = vote.whereEqualTo("Downvote", true).count();
            Upvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    if (snapshot.getCount() != 0) {
                        upvotereplycount.setText("" + snapshot.getCount());
                    } else {
                        upvotereplycount.setText("Upvote");
                    }
                }
            });

            Downvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    if (snapshot.getCount() != 0) {
                        downvotereplycount.setText("" + snapshot.getCount());
                    } else {
                        downvotereplycount.setText("Downvote");
                    }
                }
            });

            //vote history for logged in user
            if (user != null && !user.isAnonymous()) {
                DocumentReference voteReference = fStore.collection("Post").document(CommentOrigin)
                        .collection("comment").document(replyModel.replyPostOrigin)
                        .collection("reply").document(replyModel.replydocId)
                        .collection("vote")
                        .document(user.getUid());
                voteReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                        if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Upvote"))) {
                            replyupvote.setChecked(true);
                            replydownvote.setChecked(false);
                        } else if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Downvote"))) {
                            replydownvote.setChecked(true);
                            replyupvote.setChecked(false);
                        } else {
                            replyupvote.setChecked(false);
                            replydownvote.setChecked(false);
                        }
                    }
                });
            }

//            place vote
            if (user != null && !user.isAnonymous()) {
                replyupvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (replyupvote.isChecked()) {
                            Map<String, Object> votecomment = new HashMap<>();
                            votecomment.put("Upvote", true);

                            fStore.collection("Post").document(CommentOrigin)
                                    .collection("comment").document(replyModel.replyPostOrigin)
                                    .collection("reply").document(replyModel.replydocId)
                                    .collection("vote")
                                    .document(user.getUid()).set(votecomment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter3.notifyDataSetChanged();
                                        }
                                    });
                        } else if (!replyupvote.isChecked() && !replydownvote.isChecked()) {
                            fStore.collection("Post").document(CommentOrigin)
                                    .collection("comment").document(replyModel.replyPostOrigin)
                                    .collection("reply").document(replyModel.replydocId)
                                    .collection("vote")
                                    .document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter3.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
                replydownvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (replydownvote.isChecked()) {
                            Map<String, Object> votecomment = new HashMap<>();
                            votecomment.put("Downvote", true);

                            fStore.collection("Post").document(CommentOrigin)
                                    .collection("comment").document(replyModel.replyPostOrigin)
                                    .collection("reply").document(replyModel.replydocId)
                                    .collection("vote")
                                    .document(user.getUid()).set(votecomment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter3.notifyDataSetChanged();
                                        }
                                    });
                        } else if (!replyupvote.isChecked() && !replydownvote.isChecked()) {
                            fStore.collection("Post").document(CommentOrigin)
                                    .collection("comment").document(replyModel.replyPostOrigin)
                                    .collection("reply").document(replyModel.replydocId)
                                    .collection("vote")
                                    .document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter3.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
            } else {
                replyupvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowPopup();
                        replyupvote.setChecked(false);
                        replydownvote.setChecked(false);
                    }
                });
                replydownvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowPopup();
                        replyupvote.setChecked(false);
                        replydownvote.setChecked(false);
                    }
                });
            }


            //profile img per post
            StorageReference profileRef = storageReference.child("ProfileImage/" + replyAuthorUid.getText() + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_img);
                }
            });

            //reply to reply
            replybtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        replypop.setChecked(true);
                        if (user != null && !user.isAnonymous()) {
                            replyChip.setText("@" + replyModel.replyAuthor);
                            replyChipView.setText("@" + replyModel.replyAuthorUid);
                            replyChip.setVisibility(View.VISIBLE);
                            pReply.requestFocus();
                        }
                        replybtn.setChecked(false);
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter3.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter3 != null) {
            adapter3.stopListening();
        }
    }

    public void votecounter() {
        CommentModel model = (CommentModel) getArguments().getSerializable("model");

        CollectionReference vote = fStore.collection("Post").document(model.getCommentPostOrigin())
                .collection("comment").document(model.getDocId()).collection("vote");
        AggregateQuery Upvotecount = vote.whereEqualTo("Upvote", true).count();
        AggregateQuery Downvotecount = vote.whereEqualTo("Downvote", true).count();
        Upvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                AggregateQuerySnapshot snapshot = task.getResult();
                if (snapshot.getCount() != 0) {
                    upvotecount.setText("" + snapshot.getCount());
                } else {
                    upvotecount.setText("Upvote");
                }
            }
        });
        Downvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                AggregateQuerySnapshot snapshot = task.getResult();
                if (snapshot.getCount() != 0) {
                    downvotecount.setText("" + snapshot.getCount());
                } else {
                    downvotecount.setText("Downvote");
                }
            }
        });
    }

    public void ShowPopup() {
        Button registerbutton, loginButton;
        NeumorphFloatingActionButton close_popup;
        guestDialog.setContentView(R.layout.custom_popup_guest);

        close_popup = (NeumorphFloatingActionButton) guestDialog.findViewById(R.id.close_popup);
        loginButton = (Button) guestDialog.findViewById(R.id.loginButton);
        registerbutton = (Button) guestDialog.findViewById(R.id.registerbutton);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Register.class));
                getActivity().finish();
            }
        });
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        guestDialog.show();
    }

    //loading and error view
    public void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.progress_layout, null, false);
        builder.setView(v);
        alrtdialog = builder.create();
        alrtdialog.setCancelable(false);
        alrtdialog.setCanceledOnTouchOutside(false);
        alrtdialog.show();
    }

    public void hideProgressDialog() {
        alrtdialog.dismiss();
    }

    public void showEmptyView() {
        startActivity(new Intent(getActivity(), NoConnection.class));
        getActivity().finish();
    }

    //check connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}