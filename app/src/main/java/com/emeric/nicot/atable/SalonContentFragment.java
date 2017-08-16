package com.emeric.nicot.atable;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicot Emeric on 27/06/2017.
 */

public class SalonContentFragment extends Fragment {
     ListView LV;
     ListAdapter adapter;
    String mail;
    private ProgressDialog pDialog;
    private ProgressDialog pDialog2;
     JSONParser jsonParser = new JSONParser();
     JSONArray salonArray = null;
     JSONArray salonArray2 = null;
    private static String url_creation_salon = "http://192.168.1.24:80/DB/db_creation_salon.php";
    private static String url_recuperation_salon = "http://192.168.1.24:80/DB/db_recuperation_salon.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESULT = "result";
    private static final String TAG_RESULT_2 = "result2";
    private static final String TAG_SALON= "nom_salon";
    private static final String TAG_SALON_2= "nom_salon_2";
    private static final String GetSalon = "GetSalon";
    public static ArrayList<String> salon = new ArrayList<>();
    public static ArrayList<String> salon2 = new ArrayList<>();
    public static int [] imageId={R.drawable.ic_crown, R.drawable.ic_checked};
    SessionManagement session;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

                  View v =inflater.inflate(R.layout.tab_salon_list,null);

        session = new SessionManagement(getActivity());
        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        mail = user.get(SessionManagement.KEY_EMAIL);

        FloatingActionButton floatAdd = (FloatingActionButton) v.findViewById(R.id.FloatButtonAdd);
        FloatingActionButton floatRefresh = (FloatingActionButton) v.findViewById(R.id.FloatButtonRefresh);

        LV = (ListView) v.findViewById(R.id.ListView1);
        if(mail != null) {
            new GetSalon().execute(GetSalon, mail);
        }

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      public void onItemClick(AdapterView<?> parent, View view,
                                                              int position, long id) {

                                          Intent i = new Intent(getContext(), Salon.class);
                                          Object obj =  adapter.getItem(position);
                                          Integer objCount = adapter.getCount();

                                          String NomSalon = obj.toString();
                                          i.putExtra("NomSalon",NomSalon);
                                          i.putExtra("mail",mail);
                                          if(position < salon.size()){
                                              i.putExtra("tag",1);
                                          }else{
                                              i.putExtra("tag",2);
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
                        String nomsalon = edittext.getText().toString();
                        new CreateSalon().execute(nomsalon,mail);
                        dialog.dismiss();
                        new GetSalon().execute(GetSalon,mail);
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
                new GetSalon().execute(GetSalon,mail);
            }
        });
        return v;
    }





    class CreateSalon extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Création du salon...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        /**
         * Creating user in background thread
         */
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
        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String result) {
            pDialog.dismiss();
        }
    }
    class GetSalon extends AsyncTask<String, String, String> {

          /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(getActivity());
            pDialog2.setMessage("Récupération des données...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();
        }
        /**
         * Creating user in background thread
         */
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
        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            pDialog2.dismiss();
            LV.setAdapter(adapter);
        }


    }

}
