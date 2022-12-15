package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.model.DisposalModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Donate extends AppCompatActivity {

    ImageButton closedd;
    Button regNext;
    AutoCompleteTextView regDeviceType;
    EditText regBrand, regModel, regCondition, regDeviceAge;
    TextInputLayout tilBrand, tilModel, tilCondition, tilDeviceAge, tilDeviceType;
    TextView textView15;

    ArrayAdapter<String> deviceList;
    List<String> devicetypes = new ArrayList<>();

    SnapHelper snapHelper;
    FirestoreRecyclerOptions<DisposalModel> options;
    RecyclerView disposalRecycler;
    LinearLayoutManager linearLayoutManager;
    Query query;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        closedd = findViewById(R.id.closedd);
        regNext = findViewById(R.id.regNext);

        regDeviceType =findViewById(R.id.regDeviceType);
        textView15 = findViewById(R.id.textView15);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();


        closedd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //disposal locations
        disposalRecycler = findViewById(R.id.disposalRecycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        disposalRecycler.setLayoutManager(linearLayoutManager);

        disposalRecycler.setItemAnimator(null);

        query = fStore.collection("DisposalLocations")
                .orderBy("barangay", Query.Direction.ASCENDING);

        options = new FirestoreRecyclerOptions.Builder<DisposalModel>()
                .setQuery(query, DisposalModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DisposalModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DisposalModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.disposal_each, group,false);
                return new ViewHolder(view);
            }
        };

        disposalRecycler.setAdapter(adapter);
        disposalRecycler.setPadding(100,0,100,0);

        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(disposalRecycler);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    RecyclerView.ViewHolder viewHolder = disposalRecycler.findViewHolderForAdapterPosition(0);
                    RelativeLayout rrl= viewHolder.itemView.findViewById(R.id.disposalcard);
                    ImageView disposalicon = viewHolder.itemView.findViewById(R.id.disposalicon);
                    rrl.animate().scaleX(1).scaleY(1).setDuration(350).setInterpolator(new AccelerateInterpolator()).start();
                }catch (Exception ignored){

                }
            }
        }, 1000);

        disposalRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View v = snapHelper.findSnapView(linearLayoutManager);
                int pos = linearLayoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHolder = disposalRecycler.findViewHolderForAdapterPosition(pos);
                RelativeLayout rrl = viewHolder.itemView.findViewById(R.id.disposalcard);
                ToggleButton zoom = viewHolder.itemView.findViewById(R.id.zoom);
                TextView lat = viewHolder.itemView.findViewById(R.id.latitude);
                TextView lon = viewHolder.itemView.findViewById(R.id.longitude);
                ImageView disposalicon = viewHolder.itemView.findViewById(R.id.disposalicon);

                zoom.setChecked(false);
                double latitude = Double.parseDouble(lat.getText().toString());
                double longitude = Double.parseDouble(lon.getText().toString());

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    rrl.animate().setDuration(350).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                }else{
                    rrl.animate().setDuration(350).scaleX(0.75f).scaleY(0.75f).setInterpolator(new AccelerateInterpolator()).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        regDeviceType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.startListening();
                textView15.setVisibility(View.VISIBLE);
            }
        });

        DocumentReference docRef = fStore.collection("Miscellaneous").document("donationtypes");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                devicetypes = (List<String>) documentSnapshot.get("donationtags");
//                for(String log : barangay)
//                {
//                    Log.e("Tag",log);
//                }
                if (devicetypes != null) {
                    Collections.sort(devicetypes, String.CASE_INSENSITIVE_ORDER);
                    deviceList = new ArrayAdapter<String>(Donate.this, R.layout.dropdown_barangay, devicetypes);
                    regDeviceType.setAdapter(deviceList);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView disposalname, disposaladdress, latitude, longitude;
        ImageView disposalicon;
        MaterialButton track;
        ToggleButton zoom;

        DisposalModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            disposalname = itemView.findViewById(R.id.disposalname);
            disposaladdress = itemView.findViewById(R.id.disposaladdress);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longitude);
            disposalicon = itemView.findViewById(R.id.disposalicon);
            track = itemView.findViewById(R.id.track);
            zoom = itemView.findViewById(R.id.zoom);
        }

        public void bind(DisposalModel disposalModel){
            model = disposalModel;
            disposalname.setText(disposalModel.markername);
            disposaladdress.setText(disposalModel.getAddress() + ", " + disposalModel.barangay);
            latitude.setText(""+disposalModel.maplocation.getLatitude());
            longitude.setText(""+disposalModel.maplocation.getLongitude());

//           image per markers
            if (disposalModel.hasImage != null && disposalModel.hasImage){
                StorageReference profileRef = storageReference.child("MapMarkers/"+ disposalModel.docId +"/marker.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(disposalicon);
                    }
                });
            }
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
}