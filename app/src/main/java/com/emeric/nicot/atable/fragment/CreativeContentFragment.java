package com.emeric.nicot.atable.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.adapter.CustomAdapterNotif;
import com.emeric.nicot.atable.models.FirebaseSalonRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class CreativeContentFragment extends Fragment {

    private String TAG = "debug Creative TAB";
    private TextView textViewCreativeTitle, textViewCreativeTitle2;
    private LinearLayout linearLayoutCamera, linearLayoutSticker, linearLayoutImport;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_creative, null);

        textViewCreativeTitle = v.findViewById(R.id.textViewCreativeTitle);
        textViewCreativeTitle2 = v.findViewById(R.id.textViewCreativeTitle2);
        linearLayoutCamera = v.findViewById(R.id.linearlayout_camera);
        linearLayoutSticker = v.findViewById(R.id.linearlayout_sticker);
        linearLayoutImport = v.findViewById(R.id.linearlayout_import);

        textViewCreativeTitle.setText("Créer ton propre Sticker !");
        textViewCreativeTitle2.setText("Choisi comment le créer ...");

        linearLayoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        return v;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }
}
