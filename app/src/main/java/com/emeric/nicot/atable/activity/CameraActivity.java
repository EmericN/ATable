package com.emeric.nicot.atable.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraActivity extends AppCompatActivity {

    private Bitmap realImage;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.Parameters params;
    private CollectionReference mCollectionRefNotification;
    private CollectionReference mCollectionRefChat;
    private CollectionReference mCollectionRefMessage;
    private ImageButton buttonCapture, buttonSwapCamera, buttonAccept, buttonRefresh;
    private FirebaseUser user;
    private static File mediaFile;
    private List<String> focusModes;
    private Long tsLong;
    private FrameLayout mFrameLayoutPreview;
    private int mCurrentCameraId;
    RequestParams uploadParams = new RequestParams();
    private String encodedString, nomSalon, userId, userName, salonId, picUrl, dateToStr;
    private static String TAG = "debug camera";

    public void onCreate(Bundle savedInstanceState) {
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

        focusModes = params.getSupportedFocusModes();

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
                mCamera.setParameters(params);
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
                mCamera.setParameters(params);
                mCamera.startPreview();
            }
        });
    }

    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    protected void onResume() {
        super.onResume();

        buttonSwapCamera.setVisibility(View.VISIBLE);
        buttonCapture.setVisibility(View.VISIBLE);
        buttonAccept.setVisibility(View.INVISIBLE);
        buttonRefresh.setVisibility(View.INVISIBLE);

        if (mCamera == null) {
            mCamera = getCameraInstance();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
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

            final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);

                realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

                ExifInterface exif = new ExifInterface(pictureFile.toString());
                Log.d(TAG, "exif orientation : "+exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                    realImage= rotate(realImage, 90);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                    realImage= rotate(realImage, 270);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                    realImage= rotate(realImage, 180);
                }
                if((exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0") && mCurrentCameraId==1)){
                    realImage= rotate(realImage, 270);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                    realImage = rotate(realImage, 90);
                }
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

            } catch (FileNotFoundException e) {
                Log.d("Info", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }

            buttonSwapCamera.setVisibility(View.INVISIBLE);
            buttonCapture.setVisibility(View.INVISIBLE);
            buttonAccept.setVisibility(View.VISIBLE);
            buttonRefresh.setVisibility(View.VISIBLE);

            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //envoie l'image
                    tsLong = System.currentTimeMillis();
                    Date curDate = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    dateToStr = format.format(curDate);
                    uploadParams.put("filename", userName + "-" + tsLong);
                    encodeImagetoString(realImage);

                    finish();
                }
            });
        }
    };

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

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
            Log.d(TAG, "result = " + result);
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
            Log.d(TAG, "result = " + result);
        }
        camera.setDisplayOrientation(result);
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ATable");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("ATable", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                                 "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @SuppressLint("StaticFieldLeak")
    public void encodeImagetoString(final Bitmap bitmap) {

        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... params) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bitmap.recycle();
                encodedString = Base64.encodeToString(byteArray, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                // Trigger Image upload
                uploadParams.put("image", encodedString);
                imageUpload();
            }
        }.execute(null, null, null);
    }

    public void imageUpload() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://192.168.1.24/ATable/uploadPicture.php",
                uploadParams, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        ChatMessage newMessage = new ChatMessage();

                        Map<String, Object> notification = new HashMap<>();
                        notification.put("roomID", salonId);
                        notification.put("roomName", nomSalon);
                        notification.put("userName", userName);
                        notification.put("message", newMessage.text = userName + " send picture");

                        Map<String, Object> last_message = new HashMap<>();
                        last_message.put("last_message", newMessage.text =
                                userName + " send picture");
                        last_message.put("created_at", newMessage.tsLong = tsLong);

                        newMessage.text = "";
                        newMessage.idSender = userId;
                        newMessage.date = dateToStr;
                        newMessage.name = userName;
                        newMessage.emot = null;
                        newMessage.picture = userName + "-" + tsLong;
                        newMessage.tsLong = tsLong;
                        newMessage.picUrl = picUrl;

                        mCollectionRefMessage.document().set(newMessage);
                        mCollectionRefNotification.document().set(notification);
                        mCollectionRefChat.document(salonId).update(last_message);

                        mediaFile.delete();
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