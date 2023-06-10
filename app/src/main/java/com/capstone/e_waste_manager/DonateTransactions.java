package com.capstone.e_waste_manager;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.Fragments.ProfilePostFragment;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import soup.neumorphism.NeumorphFloatingActionButton;

public class DonateTransactions extends AppCompatActivity {

    ImageButton closedr;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_transactions);

        closedr = findViewById(R.id.closedr);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        guestDialog = new Dialog(this);

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Recycler = findViewById(R.id.Recycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        Recycler.setLayoutManager(linearLayoutManager);

        Recycler.setItemAnimator(null);

        DocumentReference usernameReference = fStore.collection("Users").document(user.getUid());
        usernameReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                if(Objects.equals(documentSnapShot.getString("Partner"), "0")){
                    query = fStore.collection("Donate").whereEqualTo("donationUid", userID)
                            .orderBy("donationDate", Query.Direction.DESCENDING);
                }else {
                    query = fStore.collection("Donate").whereEqualTo("markerUid", userID)
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


            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            tilpickupdate.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            DonateTransactions.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            month = month+1;
                            int daylength = (int)(Math.log10(day)+1);
                            int monthlength = (int)(Math.log10(month)+1);
                            if (daylength < 2){
                                @SuppressLint("DefaultLocale") String daychange = String.format("%02d", day);
                                if(monthlength < 2){
                                    @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                                    String date = daychange+"/"+monthchange+"/"+year;
                                    pickupdate.setText(date);
                                }else{
                                    String date = daychange+"/"+month+"/"+year;
                                    pickupdate.setText(date);
                                }

                            }else if(monthlength < 2){
                                @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                                String date = day+"/"+monthchange+"/"+year;
                                pickupdate.setText(date);
                            }else{
                                String date = day+"/"+month+"/"+year;
                                pickupdate.setText(date);
                            }
                        }
                    }, year,month,day);
                    datePickerDialog.show();
                    pickupdate.requestFocus();
                }
            });

            new DateInputMask(pickupdate);

            pickupdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View arg0, boolean hasfocus) {
                    if (hasfocus) {
                        pickupdate.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                                Dateval = s.toString().replaceAll("[^\\d./]", "");
                                if(Dateval.length() < 10){
                                    tilpickupdate.setError("required*");
                                    tilpickupdate.setErrorIconDrawable(0);
                                }else if(Dateval.length() > 10){
                                    Dateval = Dateval.substring(0, 10);
                                }else{
                                    Calendar today = Calendar.getInstance();
                                    int cYear = Integer.parseInt(pickupdate.getText().toString().substring(pickupdate.length() - 4, pickupdate.length()));
                                    int cMonth = Integer.parseInt(pickupdate.getText().toString().substring(3, 5));
                                    int cday = Integer.parseInt(pickupdate.getText().toString().substring(0, 2));

                                    if (cYear < today.get(Calendar.YEAR)) {
                                        tilpickupdate.setError("You can't go back in time!");
                                        tilpickupdate.setErrorIconDrawable(0);
                                    } else if (cMonth < today.get(Calendar.MONTH) + 1 && cYear == today.get(Calendar.YEAR)) {
                                        tilpickupdate.setError("You can't go back in time!");
                                        tilpickupdate.setErrorIconDrawable(0);
                                    } else if (cday < today.get(Calendar.DAY_OF_MONTH)) {
                                        if (cYear == today.get(Calendar.YEAR)) {
                                            tilpickupdate.setError("You can't go back in time!");
                                            tilpickupdate.setErrorIconDrawable(0);
                                        }else{
                                            tilpickupdate.setError(null);
                                        }
                                    }else if (cday == today.get(Calendar.DAY_OF_MONTH)) {
                                        if (cYear == today.get(Calendar.YEAR)) {
                                            tilpickupdate.setError("Give 1 day margin.");
                                            tilpickupdate.setErrorIconDrawable(0);
                                        }else{
                                            tilpickupdate.setError(null);
                                        }
                                    }else if (!(cday > today.get(Calendar.DAY_OF_MONTH)+1)) {
                                        if (cYear == today.get(Calendar.YEAR)) {
                                            tilpickupdate.setError("Give 1 day margin.");
                                            tilpickupdate.setErrorIconDrawable(0);
                                        }else{
                                            tilpickupdate.setError(null);
                                        }
                                    }else {
                                        tilpickupdate.setError(null);
                                    }
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
                    }
                }
            });

        }
        public void bind(DonationModel donationModel){
            date.setText("Placed on "+donationModel.donationDate.toString());
            donationid.setText("Donation ID: "+donationModel.docId);

            model = donationModel;
            DocumentReference orgReference = fStore.collection("DisposalLocations").document(donationModel.donationDocId);
            orgReference.addSnapshotListener(DonateTransactions.this, new EventListener<DocumentSnapshot>() {
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
            nameReference.addSnapshotListener(DonateTransactions.this, new EventListener<DocumentSnapshot>() {
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

            DocumentReference partnershipReference = fStore.collection("Users").document(user.getUid());
            partnershipReference.addSnapshotListener(DonateTransactions.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    if(Objects.equals(documentSnapShot.getString("Partner"), "0")){
                        if(Objects.equals(donationModel.status, "Pending")){
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setEnabled(false);
                            pendingstatus.setVisibility(View.GONE);
                            acceptedstatus.setVisibility(View.VISIBLE);
                            complete.setVisibility(View.GONE);
                        } else if(Objects.equals(donationModel.status, "Ongoing")){
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.success)));
                            pendingstatus.setVisibility(View.GONE);
                            acceptedstatus.setVisibility(View.VISIBLE);
                            complete.setVisibility(View.GONE);
                            if (Objects.equals(donationModel.method, "Pickup")){
                                resched.setVisibility(View.VISIBLE);

                                Date c = Calendar.getInstance().getTime();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = simpleDateFormat.format(c);

                                try {
                                    //Dates to compare

                                    String CurrentDate = formattedDate;
                                    String FinalDate = donationModel.pickupdate;

                                    Date date1;
                                    Date date2;

                                    SimpleDateFormat dates = new SimpleDateFormat("dd/MM/yyyy");

                                    //Setting dates
                                    date1 = dates.parse(CurrentDate);
                                    date2 = dates.parse(FinalDate);


                                    //Comparing dates
                                    long difference = Math.abs(date1.getTime() - date2.getTime());
                                    long differenceDates = difference / (24 * 60 * 60 * 1000);

                                    //Convert long to String
                                    String dayDifference = Long.toString(differenceDates);

                                    if (date1.getTime() > date2.getTime()){
                                        cancel.setEnabled(true);
                                        resched.setEnabled(true);

                                        Log.e("HERE","HERE: " + dayDifference);
                                        if(4 - differenceDates > 1){
                                            tilpickupdate.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.danger)));
                                            tilpickupdate.setHelperText("The pickup attempt has lapsed. The donation will expire after "+ (4 - differenceDates) +" days if not rescheduled.");
                                        }else if (4 - differenceDates == 1){
                                            tilpickupdate.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.danger)));
                                            tilpickupdate.setHelperText("The pickup attempt has lapsed. The donation will expire after "+ (4 - differenceDates) +" day if not rescheduled.");
                                        }else{
                                            tilpickupdate.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.gray)));
                                            tilpickupdate.setHelperText("dd/mm/yyyy");

                                            DocumentReference status = fStore.collection("Donate").document(donationModel.docId);
                                            Map<String, Object> edited = new HashMap<>();

                                            edited.put("status", "Expired");

                                            status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(DonateTransactions.this, "Donation Expired", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(DonateTransactions.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }else{
                                        cancel.setEnabled(true);
                                        resched.setEnabled(true);
                                        if (differenceDates > 2){
                                            tilpickupdate.setHelperText("You have "+ (differenceDates - 1) +" days left to CANCEL donation.");
                                            tilpickupdate.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.danger)));
                                        }else if (differenceDates > 1){
                                            tilpickupdate.setHelperText("You have "+ (differenceDates - 1) +" day left to CANCEL donation.");
                                            tilpickupdate.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.danger)));
                                        }else {
                                            tilpickupdate.setHelperText("Efforts will be made to collect the donation by the end of the day.");
                                            cancel.setEnabled(false);
                                            resched.setEnabled(false);
                                        }
                                    }

                                } catch (Exception exception) {
                                    Log.e("DIDN'T WORK", "exception " + exception);
                                }
                            }

                        } else if(Objects.equals(donationModel.status, "Received")){
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.infobs)));
                            pendingstatus.setVisibility(View.GONE);
                            acceptedstatus.setVisibility(View.GONE);
                        } else{
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.error)));
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            pendingstatus.setVisibility(View.GONE);
                            acceptedstatus.setVisibility(View.GONE);
                        }
                    }else {
                        if(Objects.equals(donationModel.status, "Pending")){
                            pickupdate.setEnabled(true);
                            pendingstatus.setVisibility(View.VISIBLE);
                            acceptedstatus.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                        } else if(Objects.equals(donationModel.status, "Ongoing")){
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.success)));
                            pendingstatus.setVisibility(View.GONE);
                            acceptedstatus.setVisibility(View.VISIBLE);
                            cancel.setVisibility(View.GONE);
                            if (Objects.equals(donationModel.method, "Pickup")){
                                resched.setVisibility(View.VISIBLE);
                            }
                        } else if(Objects.equals(donationModel.status, "Received")){
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.infobs)));
                            pendingstatus.setVisibility(View.GONE);
                            acceptedstatus.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                        }else{
                            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.error)));
                            pickupdate.setEnabled(false);
                            tilpickupdate.setEndIconVisible(false);
                            pickupdate.setText(donationModel.pickupdate);
                            pendingstatus.setVisibility(View.GONE);
                            acceptedstatus.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                        }
                    }
                }
            });



            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Objects.equals(donationModel.method, "Pickup")){
                        if(pickupdate.getText().toString().length() == 0 || !TextUtils.isEmpty(tilpickupdate.getError())) {
                            if(pickupdate.getText().toString().length() == 0)
                                pickupdate.setText("");
                            pickupdate.requestFocus();
                        }else {
                            ShowPopup2("Accept donation?", "This action cannot be undone.", donationModel.docId, "acceptpickup", pickupdate.getText().toString().trim());
                        }
                    }else {
                        ShowPopup("Accept donation?", "This action cannot be undone.", donationModel.docId, "accept");
                    }
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPopup("Decline donation?", "This action cannot be undone.", donationModel.docId, "decline");
                }
            });

            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentReference status = fStore.collection("Donate").document(donationModel.docId);
                    Map<String, Object> edited = new HashMap<>();

                    edited.put("status", "Received");

                    status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DonateTransactions.this, "Donation Received", Toast.LENGTH_SHORT).show();

                            DocumentReference status = fStore.collection("Users").document(donationModel.donationUid);
                            Map<String, Object> edited = new HashMap<>();

                            edited.put("EMPoints", FieldValue.increment(1));

                            status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DonateTransactions.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DonateTransactions.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            resched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DonateTransactions.this, tilpickupdate.getEndIconContentDescription().toString(), Toast.LENGTH_SHORT).show();

                    Calendar calendar = Calendar.getInstance();
                    final int year = calendar.get(Calendar.YEAR);
                    final int month = calendar.get(Calendar.MONTH);
                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            DonateTransactions.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            month = month+1;
                            int daylength = (int)(Math.log10(day)+1);
                            int monthlength = (int)(Math.log10(month)+1);
                            if (daylength < 2){
                                @SuppressLint("DefaultLocale") String daychange = String.format("%02d", day);
                                if(monthlength < 2){
                                    @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                                    String date = daychange+"/"+monthchange+"/"+year;
                                    pickupdate.setText(date);
                                }else{
                                    String date = daychange+"/"+month+"/"+year;
                                    pickupdate.setText(date);
                                }

                            }else if(monthlength < 2){
                                @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                                String date = day+"/"+monthchange+"/"+year;
                                pickupdate.setText(date);
                            }else{
                                String date = day+"/"+month+"/"+year;
                                tilpickupdate.setEnabled(true);
                                pickupdate.setEnabled(false);
                                pickupdate.setText(date);
                            }

                            if(!pickupdate.getText().toString().equals(donationModel.pickupdate)){
                                if(!TextUtils.isEmpty(tilpickupdate.getError())) {
                                    tilpickupdate.setEnabled(true);
                                    pickupdate.setEnabled(false);
                                    if(pickupdate.getText().toString().length() == 0)
                                        pickupdate.setText("");
                                }else {
                                    tilpickupdate.setEnabled(false);
                                    pickupdate.setEnabled(false);
                                    ShowPopup2("Reschedule pickup?", "This action cannot be undone.", donationModel.docId, "acceptpickup", pickupdate.getText().toString().trim());
                                }
                            }else{
                                tilpickupdate.setEnabled(true);
                                pickupdate.setEnabled(false);
                                tilpickupdate.setError("Select a new date to reschedule pickup.");
                            }
                        }
                    }, year,month,day);
                    datePickerDialog.show();
                    pickupdate.setEnabled(true);
                    pickupdate.requestFocus();
                    pickupdate.setEnabled(false);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPopup("Cancel donation?", "This action cannot be undone.", donationModel.docId, "cancel");
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
        new StfalconImageViewer.Builder<>(this, RESOURCES, new ImageLoader<Integer>() {
            @Override
            public void loadImage(ImageView imageView, Integer drawableRes) {
                Picasso.get().load(uri).into(imageView);
            }
        }).withTransitionFrom(target).show();
    }

    public void ShowPopup(String Title, String subtitle, String docId, String status){
        Button registerbutton, loginButton;
        NeumorphFloatingActionButton close_popup;
        guestDialog.setContentView(R.layout.custom_popup_guest);
        TextView popup_title, popup_subtitle;

        close_popup = (NeumorphFloatingActionButton) guestDialog.findViewById(R.id.close_popup);
        loginButton = (Button) guestDialog.findViewById(R.id.loginButton);
        registerbutton = (Button) guestDialog.findViewById(R.id.registerbutton);
        popup_title = (TextView) guestDialog.findViewById(R.id.popup_title);
        popup_subtitle = (TextView) guestDialog.findViewById(R.id.popup_subtitle);

        loginButton.setText("Confirm");
        registerbutton.setText("Back");
        popup_title.setText(Title);
        popup_subtitle.setText(subtitle);

        close_popup.setVisibility(View.GONE);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(status, "decline")){
                    decline(docId);
                }else if (Objects.equals(status, "accept")){
                    accept(docId);
                }else {
                    cancel(docId);
                }
                guestDialog.dismiss();
            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        guestDialog.show();
    }

    public void ShowPopup2(String Title, String subtitle, String docId, String status, String date){
        Button registerbutton, loginButton;
        NeumorphFloatingActionButton close_popup;
        guestDialog.setContentView(R.layout.custom_popup_guest);
        TextView popup_title, popup_subtitle;

        close_popup = (NeumorphFloatingActionButton) guestDialog.findViewById(R.id.close_popup);
        loginButton = (Button) guestDialog.findViewById(R.id.loginButton);
        registerbutton = (Button) guestDialog.findViewById(R.id.registerbutton);
        popup_title = (TextView) guestDialog.findViewById(R.id.popup_title);
        popup_subtitle = (TextView) guestDialog.findViewById(R.id.popup_subtitle);

        loginButton.setText("Confirm");
        registerbutton.setText("Back");
        popup_title.setText(Title);
        popup_subtitle.setText(subtitle);

        close_popup.setVisibility(View.GONE);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptpickup(docId, date);
                guestDialog.dismiss();
            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        guestDialog.show();
    }

    private void decline(String docId){
        DocumentReference status = fStore.collection("Donate").document(docId);
        Map<String, Object> edited = new HashMap<>();

        edited.put("status", "Declined");

        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DonateTransactions.this, "Donation Declined", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DonateTransactions.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancel(String docId){
        DocumentReference status = fStore.collection("Donate").document(docId);
        Map<String, Object> edited = new HashMap<>();

        edited.put("status", "Cancelled");

        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DonateTransactions.this, "Donation Cancelled", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DonateTransactions.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptpickup(String docId, String date){
        DocumentReference status = fStore.collection("Donate").document(docId);
        Map<String, Object> edited = new HashMap<>();

        edited.put("status", "Ongoing");
        edited.put("pickupdate", date);

        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DonateTransactions.this, "Donation Ongoing", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DonateTransactions.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void accept(String docId){
        DocumentReference status = fStore.collection("Donate").document(docId);
        Map<String, Object> edited = new HashMap<>();

        edited.put("status", "Ongoing");

        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DonateTransactions.this, "Donation Ongoing", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DonateTransactions.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}