package com.capstone.e_waste_manager.Fragments;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.e_waste_manager.Home;
import com.capstone.e_waste_manager.HomeAdapter;
import com.capstone.e_waste_manager.HomeInterface;
import com.capstone.e_waste_manager.HomeModel;
import com.capstone.e_waste_manager.HomeView;
import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.adapter.ProfilePostAdapter;
import com.capstone.e_waste_manager.model.ProfilePostModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ProfilePostFragment extends Fragment implements ProfilePostInterface {


    public ProfilePostFragment() {
        // Required empty public constructor
    }
    ArrayList<ProfilePostModel> PostModelArrayList;
    ProfilePostAdapter postAdapter;
    ProgressDialog pd;

    FirebaseFirestore fStore;
    RecyclerView prof_posts;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_post, container, false);

        fStore = FirebaseFirestore.getInstance();
        PostModelArrayList = new ArrayList<ProfilePostModel>();
        postAdapter = new ProfilePostAdapter(getActivity(), PostModelArrayList, this);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Fetching Data...");
        pd.show();

        prof_posts = (RecyclerView) view.findViewById(R.id.prof_posts);
        prof_posts.setHasFixedSize(true);
        prof_posts.setLayoutManager(new LinearLayoutManager(getActivity()));
        prof_posts.setAdapter(postAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        prof_posts.setLayoutManager(mLayoutManager);

        EventChangeListener();

        //swipe up to refresh.. not really needed firebase is already in real time
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });
        //not really needed

        return view;

    }
    private void EventChangeListener() {
        fStore.collection("Post").orderBy("homePostDate").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    if(pd.isShowing())
                        pd.dismiss();
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    PostModelArrayList.add(dc.getDocument().toObject(ProfilePostModel.class));
                }
                postAdapter.notifyDataSetChanged();
                DocumentSnapshot.ServerTimestampBehavior behavior = ESTIMATE;
                if(pd.isShowing())
                    pd.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), HomeView.class);

        intent.putExtra("homeTitle", PostModelArrayList.get(position).getHomeTitle());
        intent.putExtra("homeAuthor", PostModelArrayList.get(position).getHomeAuthor());
        intent.putExtra("homeBody", PostModelArrayList.get(position).getHomeBody());
        intent.putExtra("docId", PostModelArrayList.get(position).getDocId());
        intent.putExtra("homeAuthorUid", PostModelArrayList.get(position).getHomeAuthorUid());
//        intent.putExtra("homePostDate", homeModelsArrayList.get(position).getHomePostDate());

        startActivity(intent);
    }
}