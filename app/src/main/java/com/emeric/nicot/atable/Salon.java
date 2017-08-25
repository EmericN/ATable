package com.emeric.nicot.atable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by Nicot Emeric on 05/07/2017.
 */

public class Salon extends Activity {

    private static final String TAG_SUCCESS = "success";
    private static String url_invitation = "http://192.168.1.24:80/DB/db_invitation.php";
    JSONParser jsonParser = new JSONParser();
    private RecyclerView mRecyclerView, mRecyclerViewChat;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter, mAdapter2;
    private ArrayList<String> Ordre;
    private ArrayList<Integer> Image;
    private String mail, NomSalon;
    private Integer tag;
    private ArrayAdapter<String> adapter;
    private ArrayList<MessageChat> ListMessage = new ArrayList<>();
    private EditText editTextSend;
    private TextView textV1;
    private Button buttonSend;
    private ListView listViewChat;
    private ProgressDialog pDialog;
    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    private String IP_SERVER = "192.168.1.24";
    private Integer PORT = 4111;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salon);

        Bundle extras = getIntent().getExtras();
        NomSalon = extras.getString("NomSalon");
        mail = extras.getString("mail");
        tag = extras.getInt("tag");

        textV1 = (TextView) findViewById(R.id.textViewSalon);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        editTextSend = (EditText) findViewById(R.id.editTextSend);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        LinearLayoutManager mLayloutManager2 = new LinearLayoutManager(this);
        mLayloutManager2.setStackFromEnd(true);
        mRecyclerViewChat.setLayoutManager(mLayloutManager2);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapter2 = new CustomAdapterChat(getApplicationContext(), ListMessage);
        mRecyclerViewChat.setAdapter(mAdapter2);

        textV1.setText(NomSalon);

        FloatingActionButton floatAddFriend = (FloatingActionButton) findViewById(R.id.floatingActionButtonFriend);

        floatAddFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                alert.setTitle("Ajouter une personne :");
                alert.setView(edittext);
                alert.setPositiveButton("Ajouter", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String friend = edittext.getText().toString();
                        new AddFriend().execute(friend, mail, NomSalon);
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


        if (tag == 1) {


            Ordre = new ArrayList<>(Arrays.asList("A Table !", "Range ta chambre !", "Réveil toi !", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3"));
            Image = new ArrayList<>(Arrays.asList(R.drawable.ic_bubble, R.drawable.ic_checked));
            // Calling the RecyclerView
            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            // The number of Columns
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new CustomAdapter(Salon.this, Ordre, Image);
            mRecyclerView.setAdapter(mAdapter);
        } else {

            floatAddFriend.setVisibility(View.INVISIBLE);
            floatAddFriend.hide();
        }

       /* Chat chat = new Chat();
        chat.execute();*/


    }

    class AddFriend extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Salon.this);
            pDialog.setMessage("Invitation en cours...");
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
            params.put("friend", args[0]);
            params.put("mail", args[1]);
            params.put("NomSalon", args[2]);

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_invitation,
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
/*
    class Chat extends AsyncTask<Void, Void, Void> {
        private String message;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                client = new Socket(IP_SERVER, PORT);

                if (client != null) {
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                } else {
                    System.out.println("Server non up on 4111");
                }

            } catch (IOException e) {
                System.out.println("Failed to connect server " + IP_SERVER);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            buttonSend.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    message = editTextSend.getText().toString();
                    final Sender messageSender = new Sender();// Initialize chat sender AsyncTask.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        messageSender.execute();
                    }

                }
            });

            Receiver receiver = new Receiver(); // Initialize chat receiver AsyncTask.
            receiver.execute();

        }

    }

   class Receiver extends AsyncTask<Void, Void, Void> {

        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {

                    if (bufferedReader.ready()) {
                        message = bufferedReader.readLine();
                        publishProgress(null);
                    } else {
                        return null;
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            ListMessage.add(new MessageChat("Server: " + message + "\n", 2));
            mRecyclerViewChat.smoothScrollToPosition(mAdapter2.getItemCount() -1);
            mAdapter2.notifyDataSetChanged();
        }

    }

    class Sender extends AsyncTask<Void, Void, Void> {

        private String message;

        protected void onPreExecute() {
            message = editTextSend.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... args) {

            printwriter.write(message + "\n");
            printwriter.flush();

            return null;
        }

        @Override
        protected void onPostExecute(Void file_url) {
            editTextSend.setText(""); // Clear the chat box
            ListMessage.add(new MessageChat("Client: " + message + "\n", 0));
            mRecyclerViewChat.smoothScrollToPosition(mAdapter2.getItemCount() -1);
            mAdapter2.notifyDataSetChanged();
        }
    }*/
}





