package com.capstone.e_waste_manager.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.e_waste_manager.EditProfile;
import com.capstone.e_waste_manager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;


public class ProfileAboutFragment extends Fragment {

    public ProfileAboutFragment() {
        // Required empty public constructor
    }

    TextView prof_firstname, prof_lastname, prof_email, prof_addressHouse, prof_Barangay, titletext1, titletext2, titletext3;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_about, container, false);

        prof_firstname = view.findViewById(R.id.prof_firstname);
        prof_lastname = view.findViewById(R.id.prof_lastname);
        prof_email = view.findViewById(R.id.prof_email);
        prof_addressHouse = view.findViewById(R.id.prof_addressHouse);
        prof_Barangay = view.findViewById(R.id.prof_Barangay);

        titletext1 = view.findViewById(R.id.titletext1);
        titletext2 = view.findViewById(R.id.titletext2);
        titletext3 = view.findViewById(R.id.titletext3);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {

                if(Objects.equals(documentSnapShot.getString("Partner"), "1")){
                    titletext1.setText(R.string.orgprof);
                    titletext2.setText(R.string.orgname);
                    titletext3.setText(R.string.orgdesc);

                    prof_firstname.setText(documentSnapShot.getString("OrganizationName"));
                    prof_lastname.setText(documentSnapShot.getString("OrganizationDesc"));
                    prof_email.setText(documentSnapShot.getString("Email"));
                    prof_addressHouse.setText(documentSnapShot.getString("HouseAddress"));
                    prof_Barangay.setText(documentSnapShot.getString("Barangay"));
                } else{
                    prof_firstname.setText(documentSnapShot.getString("FirstName"));
                    prof_lastname.setText(documentSnapShot.getString("LastName"));
                    prof_email.setText(documentSnapShot.getString("Email"));
                    prof_addressHouse.setText(documentSnapShot.getString("HouseAddress"));
                    prof_Barangay.setText(documentSnapShot.getString("Barangay"));
                }
            }
        });


        return view;
    }


}