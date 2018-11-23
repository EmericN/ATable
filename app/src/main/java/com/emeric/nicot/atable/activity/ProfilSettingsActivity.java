package com.emeric.nicot.atable.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.emeric.nicot.atable.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class ProfilSettingsActivity extends AppCompatActivity {

    private static final String TAG = "debug profil settings ";
    private String facebookId;
    private Toolbar mToolbar;
    private ImageView imageViewProfilPicture;
    private Button buttonDisconnect, buttonChangeProfilPic;
    private FirebaseAuth mAuth;
    public static final int PICK_IMAGE = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_settings_activity);

        mAuth = FirebaseAuth.getInstance();
        imageViewProfilPicture = findViewById(R.id.imageViewProfilPicture);
        buttonDisconnect = findViewById(R.id.buttonDisconnect);
        buttonChangeProfilPic = findViewById(R.id.buttonChangeProfilPic);
        mToolbar = findViewById(R.id.toolbarProfilSettings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            facebookId = extras.getString("facebookId");
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Mon compte");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

       if(facebookId == null){
           imageViewProfilPicture.setImageResource(R.drawable.placeholderprofilpic);
           Glide.with(this)
                   .load("https://graph.facebook.com/" + facebookId + "/picture?type=large")
                   .apply(new RequestOptions()
                           .override(300, 300)
                           .circleCrop())
                   .into(new SimpleTarget<Drawable>() {
                       @Override
                       public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                           imageViewProfilPicture.setImageDrawable(resource);
                       }
                   });
       }
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                Intent i = new Intent(ProfilSettingsActivity.this, LoginChoiceActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        buttonChangeProfilPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open camera or file explorer

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            Log.d(TAG, "chosen image id : "+resultCode);
        }
    }
}