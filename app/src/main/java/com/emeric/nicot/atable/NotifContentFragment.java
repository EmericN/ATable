package com.emeric.nicot.atable;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicot Emeric on 27/06/2017.
 */

public class NotifContentFragment extends Fragment implements AdapterCallback {

    private static final String TAG_RESULT = "result";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESULT_2 = "result2";
    private static final String TAG_INVITATION = "invitation";
    private static final String TAG_INVITATION_2 = "invitation2";
    private static final String TAG_SALON = "nom_salon";
    private static final String GetSalon2 = "GetSalon";
    private static final String AcceptInvitation = "AcceptInvitation";
    private static final String GetInvitation = "GetInvitation";
    public static ArrayList<String> invitation = new ArrayList<>();
    public static ArrayList<String> invitation2 = new ArrayList<>();
    public static ArrayList<String> nomsalon = new ArrayList<>();
    private static String url_accept_invitation = "http://192.168.1.24:80/DB/db_accept_invitation.php";
    private static String url_recuperation_invitation = "http://192.168.1.24:80/DB/db_recuperation_invitation.php";
    ListView LV;
    SessionManagement session;
    String mail;
    JSONArray InvitArray = null;
    JSONArray NomSalonArray = null;
    JSONParser jsonParser = new JSONParser();
    ListAdapter adapter;
    private ProgressDialog pDialog;

    public void onMethodCallback(String NomSalon) {
        new AcceptInvitation().execute(AcceptInvitation, mail, NomSalon);
        new GetInvitation().execute(GetInvitation, mail);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_notification_list, null);


        LV = (ListView) v.findViewById(R.id.ListView1);

        session = new SessionManagement(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        mail = user.get(SessionManagement.KEY_EMAIL);

        if (mail != null) {
            new GetInvitation().execute(GetInvitation, mail);
        }

        return v;
    }

    class GetInvitation extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating user in background thread
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("GetInvitation", args[0]);
            params.put("mail", args[1]);
            System.out.println(params.get("mail"));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_recuperation_invitation,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag

            try {
                // InvitArray = json.getJSONArray(TAG_RESULT);
                NomSalonArray = json.getJSONArray(TAG_RESULT_2);

                // invitation.clear();
                // invitation2.clear();
                nomsalon.clear();

               /* for (int i = 0; i < InvitArray.length(); i++) {
                    JSONObject c = InvitArray.getJSONObject(i);
                    String nom_salon = c.getString(TAG_INVITATION);
                    String nom_salon_2 = c.getString(TAG_INVITATION_2);
                   invitation.add(nom_salon);
                    invitation2.add(nom_salon_2);
                }  */

                for (int i = 0; i < NomSalonArray.length(); i++) {
                    JSONObject c = NomSalonArray.getJSONObject(i);
                    String nom_salon_2 = c.getString(TAG_SALON);
                    nomsalon.add(nom_salon_2);
                }

                adapter = new CustomAdapterNotif(getContext(), nomsalon, NotifContentFragment.this);

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
            LV.setAdapter(adapter);
        }


    }

    class AcceptInvitation extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Récupération des données...");
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
            params.put("AcceptInvitation", args[0]);
            params.put("mail", args[1]);
            params.put("NomSalon", args[2]);
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_accept_invitation,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            int success = 0;
            try {
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (success == 1) {

                return "success";
            }
            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            //LV.setAdapter(adapter);
        }


    }
}
