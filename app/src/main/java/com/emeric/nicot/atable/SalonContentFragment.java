package com.emeric.nicot.atable;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Nicot Emeric on 27/06/2017.
 */

public class SalonContentFragment extends Fragment {
    public static int[] imageId = {R.drawable.ic_crown, R.drawable.ic_checked};
    public ArrayList<FirebaseSalon> salon;
    ListView LV;
    String mail;
    CustomAdapterSalon adapter;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRefRelationship, myRefMessage, myRefChat, myRefGetChat, myRefGetChatTs;
    private String userId, ts;
    private Long tsLong;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRefRelationship = database.getReference("relationship");
        myRefChat = database.getReference("chats");
        myRefMessage = database.getReference("messages");
        tsLong = System.currentTimeMillis();
        ts = tsLong.toString();
        salon = new ArrayList<FirebaseSalon>();

        View v = inflater.inflate(R.layout.tab_salon_list, null);
        FloatingActionButton floatAdd = (FloatingActionButton) v.findViewById(R.id.FloatButtonAdd);
        LV = (ListView) v.findViewById(R.id.ListView1);


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();

        } else {

        }

        adapter = new CustomAdapterSalon(getContext(), R.layout.list_item, salon);
        LV.setAdapter(adapter);

        //Charge tous les salons proprietaire firebase
        myRefGetChatTs = database.getReference().child("relationship");
        myRefGetChatTs.orderByChild(userId).equalTo("admin").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String salonAdminTs = postSnapshot.getKey();

                    myRefGetChat = database.getReference("chats/" + salonAdminTs + "/");
                    myRefGetChat.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                String salonAdmin = postSnapshot.getKey();
                                FirebaseSalon addedsalon = new FirebaseSalon(salonAdmin);

                                salon.add(addedsalon);
                                Log.d("list1", salon.toString());
                                adapter.notifyDataSetChanged();

                            }
                            //adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getContext(), Salon.class);
                FirebaseSalon salonAdmin = salon.get(position);
                i.putExtra("NomSalon", salonAdmin.getSalon());
                i.putExtra("mail", mail);
                if (position < salon.size()) {
                    i.putExtra("tag", 1);
                } else {
                    i.putExtra("tag", 2);
                }
                startActivity(i);
            }
        });

        floatAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                alert.setTitle("Nom du salon");
                alert.setView(edittext);
                alert.setPositiveButton("Créer", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nomsalon = edittext.getText().toString();

                        //myRefChat = /chats/

                        myRefChat.child(ts).child(nomsalon).setValue("", new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                myRefRelationship.child(ts).child(userId).setValue("admin", new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        myRefMessage.child(ts).setValue("", new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                FirebaseSalon salonAdd = new FirebaseSalon(nomsalon);
                                                salon.add(salonAdd);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        dialog.dismiss();

                    }
                });

                alert.setNegativeButton("Quitter", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                edittext.setLayoutParams(lp);
                alert.setView(edittext);
                final AlertDialog dialog = alert.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(false);

                edittext.addTextChangedListener(new TextWatcher() {
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });
            }
        });

        return v;
    }
}
