package com.emeric.nicot.atable;


import android.app.ProgressDialog;
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
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Nicot Emeric on 27/06/2017.
 */

public class SalonContentFragment extends Fragment {
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESULT = "result";
    private static final String TAG_RESULT_2 = "result2";
    private static final String TAG_SALON = "nom_salon";
    private static final String TAG_SALON_2 = "nom_salon_2";
    private static final String GetSalon = "GetSalon";
    public static ArrayList<String> salon2 = new ArrayList<>();
    public static ArrayList<String> salonTest = new ArrayList<>();
    public static int[] imageId = {R.drawable.ic_crown, R.drawable.ic_checked};
    private static String url_creation_salon = "http://192.168.1.24:80/DB/db_creation_salon.php";
    private static String url_recuperation_salon = "http://192.168.1.24:80/DB/db_recuperation_salon.php";
    public ArrayList<FirebaseSalon> salon;
    ListView LV;
    String mail;
    ListAdapter adapter;
    JSONParser jsonParser = new JSONParser();
    JSONArray salonArray = null;
    JSONArray salonArray2 = null;
    SessionManagement session;
    private String TAG = "Check authent", valueId2;
    private ProgressDialog pDialog;
    private ProgressDialog pDialog2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRefRelationship, myRefMessage, myRefChat, myRefRelationshipUser, myRefGetChat, myRefGetChatTs;
    private QueryFirebase mQueryFirebase;
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
        FloatingActionButton floatRefresh = (FloatingActionButton) v.findViewById(R.id.FloatButtonRefresh);
        LV = (ListView) v.findViewById(R.id.ListView1);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();

            adapter = new CustomAdapterSalon(getContext(), R.layout.list_item, salon);
            LV.setAdapter(adapter);
            loadSalon();
        } else {

            Intent i = new Intent(v.getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            // closing this screen
            getActivity().finish();

        }



      /*  myRefRelationshipUser = database.getReference("relationship/"+userId+"/user/");
        myRefRelationshipUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                salon2.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    SalonContentFragment.UserSalon salonUserSalon = postSnapshot.getValue(SalonContentFragment.UserSalon.class);
                    System.out.println("SALON USER ! :" + salonUserSalon.getSalonUser());
                    salon2.add(salonUserSalon.getSalonUser());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getContext(), Salon.class);
                Object obj = adapter.getItem(position);
                Integer objCount = adapter.getCount();

                String NomSalon = obj.toString();
                i.putExtra("NomSalon", NomSalon);
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

                        // new CreateSalon().execute(nomsalon, mail);
                        // new GetSalon().execute(GetSalon, mail);

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

                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
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
                            ((AlertDialog) dialog).getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            ((AlertDialog) dialog).getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });
            }
        });

        floatRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new GetSalon().execute(GetSalon, mail);

            }
        });
        return v;
    }


    public void loadSalon() {

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

                            }
                           /* adapter.notifyDataSetChanged();*/
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
    }


   /* class CreateSalon extends AsyncTask<String, String, String> {
        *//**
     * Before starting background thread Show Progress Dialog
     *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Création du salon...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        *//**
     * Creating user in background thread
     *//*
        protected String doInBackground(String... args) {
            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("nomsalon", args[0]);
            params.put("mail", args[1]);

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_creation_salon,
                    "POST", params);
            // check log cat fro response
            Log.d("Create Response", json.toString());
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created user
                    return "success";
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        *//**
     * After completing background task Dismiss the progress dialog
     **//*
        protected void onPostExecute(String result) {
            pDialog.dismiss();
        }
    }

    class GetSalon extends AsyncTask<String, String, String> {

        *//**
     * Before starting background thread Show Progress Dialog
     *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(getActivity());
            pDialog2.setMessage("Récupération des données...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();
        }

        *//**
     * Creating user in background thread
     *//*
        protected String doInBackground(String... args) {
            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("GetSalon", args[0]);
            params.put("mail", args[1]);
            System.out.println(params.get("mail"));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_recuperation_salon,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag

            try {
                salonArray = json.getJSONArray(TAG_RESULT);
                salonArray2 = json.getJSONArray(TAG_RESULT_2);

                salon.clear();
                salon2.clear();

                for (int i = 0; i < salonArray.length(); i++) {
                    JSONObject c = salonArray.getJSONObject(i);
                    String nom_salon = c.getString(TAG_SALON);
                    salon.add(nom_salon);
                }
                for (int i = 0; i < salonArray2.length(); i++) {
                    JSONObject c = salonArray2.getJSONObject(i);
                    String nom_salon_2 = c.getString(TAG_SALON_2);
                    salon2.add(nom_salon_2);
                }

                adapter = new CustomAdapterSalon(getContext(), salon, salon2, imageId);

            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("test erreur n");
            }

            return null;

        }

        *//**
     * After completing background task Dismiss the progress dialog
     **//*
        protected void onPostExecute(String file_url) {
            pDialog2.dismiss();
            LV.setAdapter(adapter);
        }

    }*/

}
