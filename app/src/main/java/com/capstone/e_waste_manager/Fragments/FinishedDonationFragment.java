package com.capstone.e_waste_manager.Fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.e_waste_manager.DateInputMask;
import com.capstone.e_waste_manager.DonateTransactions;
import com.capstone.e_waste_manager.GoogleMapView;
import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.model.DonationModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import soup.neumorphism.NeumorphFloatingActionButton;

public class FinishedDonationFragment extends Fragment {

    SwipeRefreshLayout swipeRefresh;
    RecyclerView Recycler;

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;

    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    Dialog guestDialog;

    Query query;

    String Dateval;

    public FinishedDonationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finished_donation, container, false);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        guestDialog = new Dialog(view.getContext());

        Recycler = view.findViewById(R.id.Recycler);
        linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        Recycler.setLayoutManager(linearLayoutManager);

        Recycler.setItemAnimator(null);

        DocumentReference usernameReference = fStore.collection("Users").document(user.getUid());
        usernameReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                if(Objects.equals(documentSnapShot.getString("Partner"), "0")){
                    query = fStore.collection("Donate").whereEqualTo("donationUid", userID)
                            .whereIn("status", Arrays.asList("Received", "Cancelled", "Declined", "Expired"))
                            .orderBy("donationDate", Query.Direction.DESCENDING);
                }else {
                    query = fStore.collection("Donate").whereEqualTo("markerUid", userID)
                            .whereIn("status", Arrays.asList("Received", "Cancelled", "Declined", "Expired"))
                            .orderBy("donationDate", Query.Direction.DESCENDING);
                }
                FirestoreRecyclerOptions<DonationModel> options = new FirestoreRecyclerOptions.Builder<DonationModel>()
                        .setQuery(query, DonationModel.class)
                        .build();

                adapter = new FirestoreRecyclerAdapter<DonationModel, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DonationModel model) {
                        holder.bind(model);
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                        View view = LayoutInflater.from(group.getContext())
                                .inflate(R.layout.donation_each, group,false);
                        return new ViewHolder(view);
                    }
                };

                Recycler.setAdapter(adapter);
                adapter.startListening();
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

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView orgname, brandandmodel, condition, deviceage, date, donationid, orgaddress;
        TextView name, number, address;
        Chip status, devicetype;
        ConstraintLayout pickupcard, dropoffcard;
        LinearLayout pendingstatus, acceptedstatus;
        DonationModel model;
        TextInputLayout tilpickupdate;
        EditText pickupdate;
        Button accept, decline, complete, cancel, track, resched;
        ImageView imageView6;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            orgname = itemView.findViewById(R.id.orgname);
            brandandmodel = itemView.findViewById(R.id.brandandmodel);
            condition = itemView.findViewById(R.id.condition);
            deviceage = itemView.findViewById(R.id.deviceage);

            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            address = itemView.findViewById(R.id.address);

            status = itemView.findViewById(R.id.status);
            devicetype = itemView.findViewById(R.id.devicetype);

            pickupcard = itemView.findViewById(R.id.pickupcard);
            dropoffcard = itemView.findViewById(R.id.dropoffcard);

            pendingstatus = itemView.findViewById(R.id.pendingstatus);
            acceptedstatus = itemView.findViewById(R.id.acceptedstatus);

            donationid = itemView.findViewById(R.id.donationid);

            tilpickupdate = itemView.findViewById(R.id.tilpickupdate);
            pickupdate = itemView.findViewById(R.id.pickupdate);

            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
            complete = itemView.findViewById(R.id.complete);
            cancel = itemView.findViewById(R.id.cancel);
            resched = itemView.findViewById(R.id.resched);

            imageView6 = itemView.findViewById(R.id.imageView6);

            orgaddress = itemView.findViewById(R.id.orgaddress);

            track = itemView.findViewById(R.id.track);

        }
        public void bind(DonationModel donationModel){
            date.setText("Placed on "+donationModel.donationDate.toString());
            donationid.setText("Donation ID: "+donationModel.docId);

            model = donationModel;
            DocumentReference orgReference = fStore.collection("DisposalLocations").document(donationModel.donationDocId);
            orgReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    orgname.setText(documentSnapShot.getString("markername"));
                    orgaddress.setText(documentSnapShot.getString("address")+ ", " + documentSnapShot.getString("barangay"));
                    GeoPoint geoPoint = documentSnapShot.getGeoPoint("maplocation");
                    track.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(itemView.getContext(), GoogleMapView.class);
                            intent.putExtra("latitude1", geoPoint.getLatitude());
                            intent.putExtra("longitude1", geoPoint.getLongitude());
                            itemView.getContext().startActivity(intent);
                        }
                    });
                }
            });
            status.setText(donationModel.status);

            String inbrand = donationModel.brand;
            String inmodel = donationModel.model;
            brandandmodel.setText(inbrand+" "+inmodel);

            condition.setText(donationModel.condition);
            deviceage.setText(donationModel.deviceAge);
            devicetype.setText(donationModel.deviceType);

            DocumentReference nameReference = fStore.collection("Users").document(donationModel.donationUid);
            nameReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    name.setText(documentSnapShot.getString("FirstName").substring(0, 1).toUpperCase()
                            + documentSnapShot.getString("FirstName").substring(1)+ " " +
                            documentSnapShot.getString("LastName").substring(0, 1).toUpperCase()
                            + documentSnapShot.getString("LastName").substring(1));
                }
            });
            number.setText("+63 " + donationModel.number);
            String docaddress = donationModel.address;
            String docbarangay = donationModel.barangay;
            address.setText(docaddress+ ", "+ docbarangay);

            if(Objects.equals(donationModel.method, "Pickup")){
                pickupcard.setVisibility(View.VISIBLE);
                dropoffcard.setVisibility(View.GONE);
                address.setVisibility(View.VISIBLE);
            }else{
                pickupcard.setVisibility(View.GONE);
                dropoffcard.setVisibility(View.VISIBLE);
                address.setVisibility(View.GONE);
            }
            pendingstatus.setVisibility(View.GONE);
            acceptedstatus.setVisibility(View.GONE);

            DocumentReference partnershipReference = fStore.collection("Users").document(user.getUid());
            partnershipReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    if(Objects.equals(documentSnapShot.getString("Partner"), "0")){
                        if(Objects.equals(donationModel.status, "Received")){
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.infobs)));
                            tilpickupdate.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.gray)));
                            tilpickupdate.setHelperText("dd/mm/yyyy");
                        } else{
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.error)));
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                        }
                    }else {
                        if(Objects.equals(donationModel.status, "Received")){
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.infobs)));
                            tilpickupdate.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.gray)));
                            tilpickupdate.setHelperText("dd/mm/yyyy");
                        }else{
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.error)));
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                        }
                    }
                }
            });


            //image per donation
            StorageReference profileRef = storageReference.child("Donate/"+donationModel.docId+"/donateimg.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(imageView6);
                    imageView6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showViewer(imageView6, uri);
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "No Profile Image");
                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void refresh(){
        swipeRefresh.setRefreshing(true);
        Recycler.setAdapter(null);
        Recycler.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    private static final Integer[] RESOURCES = new Integer[]{R.drawable.shape_placeholder};

    private void showViewer(ImageView target, Uri uri) {
        new StfalconImageViewer.Builder<>(getContext(), RESOURCES, new ImageLoader<Integer>() {
            @Override
            public void loadImage(ImageView imageView, Integer drawableRes) {
                Picasso.get().load(uri).into(imageView);
            }
        }).withTransitionFrom(target).show();
    }
}