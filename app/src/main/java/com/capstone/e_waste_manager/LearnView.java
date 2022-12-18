package com.capstone.e_waste_manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTelegram;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;
import soup.neumorphism.NeumorphCardView;

public class LearnView extends AppCompatActivity implements DownloadFile.Listener{

    ImageButton backButton;
    ImageView cover, imageView2, imageView3;
    TextView lTitle, lAuthor, lBody, lSubTitle;
    RichLinkViewTelegram urllink;

    Button downloadPDF;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseUser user;

    NeumorphCardView ebook;

    //pdf
    private RemotePDFViewPager remotePDFViewPager;

    private PDFPagerAdapter pdfPagerAdapter;

    private String url;

    private ProgressBar progressBar;

    private LinearLayout pdfLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnview);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        String title = getIntent().getStringExtra("learnTitle");
        String author = getIntent().getStringExtra("learnAuthor");
        String body = getIntent().getStringExtra("learnBody");
        String docId = getIntent().getStringExtra("docId");
        String url = getIntent().getStringExtra("url");
        String filepdf = getIntent().getStringExtra("filepdf");
        String learnSubTitle = getIntent().getStringExtra("learnSubTitle");

        lTitle = findViewById(R.id.lTitle);
        lAuthor = findViewById(R.id.lAuthor);
        lBody = findViewById(R.id.lBody);
        backButton = findViewById(R.id.backButton);
        cover = findViewById(R.id.cover);
        lSubTitle = findViewById(R.id.lSubTitle);
        urllink = findViewById(R.id.urllink);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        ebook = findViewById(R.id.ebook);
        downloadPDF = findViewById(R.id.downloadPDF);

        backButton.setOnClickListener(v -> onBackPressed());

        lTitle.setText(title);

        lBody.setText(body);
        lSubTitle.setText(learnSubTitle);

        DocumentReference usernameReference = fStore.collection("Users").document(author);
        usernameReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                lAuthor.setText(documentSnapShot.getString("Username"));
            }
        });

        if (lSubTitle.getText().equals("")){
            lSubTitle.setVisibility(View.GONE);
        }

        if(url != null){
            urllink.setLink(url, new ViewListener() {
                @Override
                public void onSuccess(boolean status) {
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }

        StorageReference coverRef = storageReference.child("LearningMaterial/"+docId+"/coverimg.jpg");
        coverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(cover);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        progressBar = findViewById(R.id.progressBar);

        if (filepdf != null){
            //set the Visibility of the progressbar to visible
            progressBar.setVisibility(View.VISIBLE);
            StorageReference pdfRef = storageReference.child("LearningMaterial/"+docId+"/"+filepdf);
            pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    remotePDFViewPager = new RemotePDFViewPager(LearnView.this, uri.toString(), (DownloadFile.Listener) LearnView.this);

                    downloadPDF.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                            startActivity(urlIntent);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

            //initialize the pdfLayout
            pdfLayout = findViewById(R.id.pdf_layout);


            pdfLayout.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }else{
            ebook.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            downloadPDF.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {

        // That's the positive case. PDF Download went fine
        pdfPagerAdapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(pdfPagerAdapter);
        updateLayout();
        progressBar.setVisibility(View.GONE);
    }

    private void updateLayout() {

        pdfLayout.addView(remotePDFViewPager,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onFailure(Exception e) {
        // This will be called if download fails
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        // You will get download progress here
        // Always on UI Thread so feel free to update your views here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (pdfPagerAdapter != null) {
            pdfPagerAdapter.close();
        }
    }
}
