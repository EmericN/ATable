package com.emeric.nicot.atable.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.ChatMessage;
import com.emeric.nicot.atable.services.CameraPreview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.Parameters params;
    private CollectionReference mCollectionRefNotification;
    private CollectionReference mCollectionRefChat;
    private CollectionReference mCollectionRefMessage;
    private ImageButton buttonCapture, buttonSwapCamera, buttonAccept, buttonRefresh;
    private FirebaseUser user;
    private FrameLayout mFrameLayoutPreview;
    private int mCurrentCameraId;
    RequestParams uploadParams = new RequestParams();
    private String encodedString, nomSalon, userId, userName, salonId, picUrl;
    private String TAG = "debug camera";

    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "passage onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nomSalon = extras.getString("nomSalon");
            userId = extras.getString("userId");
            salonId = extras.getString("salonId");
            userName = extras.getString("userName");
            picUrl = extras.getString("picUrl");
        }

        buttonCapture = findViewById(R.id.button_capture);
        ImageButton buttonReturn = findViewById(R.id.button_return);
        buttonSwapCamera = findViewById(R.id.button_front_cam);
        buttonAccept = findViewById(R.id.button_accept);
        buttonRefresh = findViewById(R.id.button_delete);

        // Create our Preview view and set it as the content of our activity.
        mFrameLayoutPreview = findViewById(R.id.camera_preview);

        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mCollectionRefMessage = mFirestore.collection("chats").document(salonId).collection("messages");
        mCollectionRefNotification = mFirestore.collection("notifications");
        mCollectionRefChat = mFirestore.collection("chats");

        mCamera = getCameraInstance();
        mCamera.setPreviewCallback(null);
        setCameraDisplayOrientation(CameraActivity.this, 0, mCamera);
        params = mCamera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();

        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(params);

        mPreview = new CameraPreview(this, mCamera);
        mFrameLayoutPreview.addView(mPreview);

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);


            }
        });

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSwapCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mCamera.stopPreview();
                mCamera.release();

                //swap the id of the camera to be used
                if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                mCamera = Camera.open(mCurrentCameraId);
                setCameraDisplayOrientation(CameraActivity.this, mCurrentCameraId, mCamera);
                try {
                    mCamera.setPreviewDisplay(mPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSwapCamera.setVisibility(View.VISIBLE);
                buttonCapture.setVisibility(View.VISIBLE);
                buttonAccept.setVisibility(View.INVISIBLE);
                buttonRefresh.setVisibility(View.INVISIBLE);
                mCamera.startPreview();
            }
        });
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Passage onPause !");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Passage onResume !");

        if (mCamera == null) {
            mCamera = getCameraInstance();
            mCamera.setParameters(params);
            setCameraDisplayOrientation(CameraActivity.this, 0, mCamera);
            mPreview = new CameraPreview(this, mCamera);
            mFrameLayoutPreview.addView(mPreview);
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {

            buttonSwapCamera.setVisibility(View.INVISIBLE);
            buttonCapture.setVisibility(View.INVISIBLE);
            buttonAccept.setVisibility(View.VISIBLE);
            buttonRefresh.setVisibility(View.VISIBLE);

            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //envoie l'image
                    Long tsLong = System.currentTimeMillis();
                    Date curDate = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String DateToStr = format.format(curDate);

                    uploadParams.put("filename", userName+"-"+tsLong);
                    encodeImagetoString(data);

                    ChatMessage newMessage = new ChatMessage();

                    Map<String, Object> notification = new HashMap<>();
                    notification.put("roomID", salonId);
                    notification.put("roomName", nomSalon);
                    notification.put("userName", userName);
                    notification.put("message", newMessage.text = userName+" send picture");

                    Map<String, Object> last_message = new HashMap<>();
                    last_message.put("last_message", newMessage.text = userName+" send picture");
                    last_message.put("created_at", newMessage.tsLong = tsLong);


                    newMessage.text = "";
                    newMessage.idSender = userId;
                    newMessage.date = DateToStr;
                    newMessage.name = userName;
                    newMessage.emot = null;
                    newMessage.picture = userName+"-"+tsLong;
                    newMessage.tsLong = tsLong;
                    newMessage.picUrl = picUrl;

                    mCollectionRefMessage.document().set(newMessage);
                    mCollectionRefNotification.document().set(notification);
                    mCollectionRefChat.document(salonId).update(last_message);

                    finish();
                }
            });
        }
    };

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void encodeImagetoString(final byte[] data) {

        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... params) {
                // Encode Image to String
                encodedString = Base64.encodeToString(data, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                // Trigger Image upload
                uploadParams.put("image", encodedString);
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    public void makeHTTPCall(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://192.168.1.24/ATable/uploadPicture.php",
                uploadParams, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                       /* Toast.makeText(CameraActivity.this, statusCode,
                                Toast.LENGTH_LONG).show();*/
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                          Throwable error) {
                        if (statusCode == 404) {
                            Toast.makeText(CameraActivity.this,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(CameraActivity.this,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    CameraActivity.this,
                                    "Error Occured n Most Common Error: n1. Device not " +
                                    "connected to Internetn2. Web App is not deployed in App " +
                                    "servern3. App server is not runningn HTTP Status code : "
                                    + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                });
    }
}