package com.capstone.e_waste_manager.Fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.anupkumarpanwar.scratchview.ScratchView;
import com.capstone.e_waste_manager.EditProfile;
import com.capstone.e_waste_manager.Home;
import com.capstone.e_waste_manager.HomeModel;
import com.capstone.e_waste_manager.MyRewardsModel;
import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.RewardsCatalog;
import com.capstone.e_waste_manager.RewardsModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MyRewardsFragment extends Fragment {

    public MyRewardsFragment() {
        // Required empty public constructor
    }

    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestorePagingAdapter<MyRewardsModel, ViewHolder> adapter;
    FirebaseUser user;

    SwipeRefreshLayout swipeRefresh;

    Query query;
    PagingConfig config;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        recycler = view.findViewById(R.id.Recycler);

        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);

        recycler.setItemAnimator(null);

        //forums
        query = fStore.collection("Vouchers").whereEqualTo("rewardUid",user.getUid()).limit(3);

        config = new PagingConfig(/* page size */ 2, /* prefetchDistance */ 2,
                /* enablePlaceHolders */ false);

        FirestorePagingOptions<MyRewardsModel> options = new FirestorePagingOptions.Builder<MyRewardsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, MyRewardsModel.class)
                .build();

        adapter =
                new FirestorePagingAdapter<MyRewardsModel, ViewHolder>(options) {
                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create the ItemViewHolder
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.myreward_each, parent,false);
                        return new ViewHolder(view);
                        // ...
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder,
                                                    int position,
                                                    @NonNull MyRewardsModel model) {
                        // Bind the item to the view holder
                        holder.bind(model);
                        // ...
                    }


                };

        recycler.setAdapter(adapter);

        adapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates states) {
                LoadState refresh = states.getRefresh();
                LoadState append = states.getAppend();

                if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
                    // The previous load (either initial or additional) failed. Call
                    // the retry() method in order to retry the load operation.
                    // ...
                    adapter.retry();
                }

                if (refresh instanceof LoadState.Loading) {
                    // The initial Load has begun
                    // ...
                }

                if (append instanceof LoadState.Loading) {
                    // The adapter has started to load an additional page
                    // ...
                }

                if (append instanceof LoadState.NotLoading) {
                    LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                    if (notLoading.getEndOfPaginationReached()) {
                        // The adapter has finished loading all of the data set
                        // ...
                        return null;
                    }

                    if (refresh instanceof LoadState.NotLoading) {
                        // The previous load (either initial or additional) completed
                        // ...
                        return null;
                    }
                }
                return null;
            }
        });

        //swipe up to refresh
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        MyRewardsModel model;
        TextView rewardtitle, rewardexp, code;
        ScratchView scratchView;
        LottieAnimationView scratchanim;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rewardtitle = itemView.findViewById(R.id.rewardtitle);
            rewardexp = itemView.findViewById(R.id.rewardexp);
            code = itemView.findViewById(R.id.code);
            scratchView = itemView.findViewById(R.id.scratchView);
            scratchanim = itemView.findViewById(R.id.scratchanim);

            code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = getSystemService(getContext(), ClipboardManager.class);
                    ClipData clip = ClipData.newPlainText("Voucher code", code.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
            });

            scratchView.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            scratchView.setRevealListener(new ScratchView.IRevealListener() {
                @Override
                public void onRevealed(ScratchView scratchView) {
                    scratchView.animate()
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    scratchView.setVisibility(View.GONE);
                                }
                            });
                    DocumentReference docRef = fStore.collection("Vouchers").document(model.docId);
                    Map<String, Object> edited = new HashMap<>();

                    edited.put("revealed", true);

                    docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

                @Override
                public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                    if (percent>=0.5) {
                        Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                    }
                }
            });
            scratchanim.animate()
                    .alpha(0.0f)
                    .setStartDelay(3000)
                    .setDuration(1000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            scratchanim.setVisibility(View.GONE);
                        }
                    });

        }
        public void bind(MyRewardsModel myrewardsModel){
            model = myrewardsModel;

            code.setText(model.code);

            if (!model.revealed){
                scratchView.setVisibility(View.VISIBLE);
                scratchanim.setVisibility(View.VISIBLE);
            }else {
                scratchView.setVisibility(View.GONE);
                scratchanim.setVisibility(View.GONE);
            }

            DocumentReference documentReference = fStore.collection("Reward").document(model.rewardId);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    assert documentSnapShot != null;

                    rewardtitle.setText(documentSnapShot.getString("title"));
                    rewardexp.setText("Expires on "+documentSnapShot.getString("exp"));

                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void refresh(){
        swipeRefresh.setRefreshing(true);
        adapter.refresh();
        swipeRefresh.setRefreshing(false);
    }
}