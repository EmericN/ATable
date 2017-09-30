package com.emeric.nicot.atable;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;


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
    ListView LV;
    SessionManagement session;
    String mail;
    JSONArray InvitArray = null;
    JSONArray NomSalonArray = null;
    ListAdapter adapter;
    private ProgressDialog pDialog;

    public void onMethodCallback(String NomSalon) {


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_notification_list, null);


        LV = (ListView) v.findViewById(R.id.ListView1);

        session = new SessionManagement(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        mail = user.get(SessionManagement.KEY_EMAIL);

        if (mail != null) {

        }

        return v;
    }
}
