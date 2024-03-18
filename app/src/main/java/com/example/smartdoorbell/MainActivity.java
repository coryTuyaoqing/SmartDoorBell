package com.example.smartdoorbell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnProfile;
    private TextView txtDoorStatus;
    private ImageView cameraPhoto;

    private MediaRecorder mediaRecorder;
    public static String fileName = "recorded.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;

    private ImageButton imgBtnLock, imgBtnSpeak, imgBtnCamera;
    private boolean doorClosed, speakable;
    private OkHttpClient client;
    final private String url = "http://192.168.1.103/photo";
    private static final String url_audio = "http://192.168.1.103/upload_audio"; //CHANGE THIS!!!!!
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permission already granted, do nothing
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Request RECORD_AUDIO permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQUEST_CODE);

        } else {
            // Permission is already granted, start recording
            startRecording();
        }

        intiView();
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                UserInfo userInfo = new UserInfo(getApplicationContext());
                if(userInfo.initInfo()){
                    intent  = new Intent(MainActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                }
                else{
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            }
        });

        imgBtnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doorClosed) {
                    imgBtnLock.setImageResource(R.drawable.ic_unlock);
                    txtDoorStatus.setText("OPENED");
                    doorClosed = false;
                } else {
                    imgBtnLock.setImageResource(R.drawable.ic_lock);
                    txtDoorStatus.setText("CLOSED");
                    doorClosed = true
                    ;
                }
            }
        });

        imgBtnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speakable){
                    imgBtnSpeak.setImageResource(R.drawable.ic_deafen);
                    speakable = false;
                    stopAudio();
                    uploadAudioFile(file);
                }
                else{
                    imgBtnSpeak.setImageResource(R.drawable.ic_speak);
                    speakable = true;
                    startRecording();
                }
            }

            private void stopAudio() {
                mediaRecorder.stop();
                mediaRecorder.release();
            }

            private void uploadAudioFile(final String filePath) {
                final File audioFile = new File(filePath);
                if (!audioFile.exists()) {
                    Log.e("MainActivity", "Audio file not found");
                    return;
                }

                // Create a multipart request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", audioFile.getName(),
                                RequestBody.create(audioFile, MediaType.parse("audio/3gpp")))
                        .build();

                Request request = new Request.Builder()
                        .url(url_audio)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                        Log.e("MainActivity", "Failed to upload audio", e);
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            // Handle successful upload
                            Log.d("MainActivity", "Audio uploaded successfully");
                        } else {
                            // Handle failed upload
                            Log.e("MainActivity", "Failed to upload audio: " + response.code());
                        }
                    }
                });
            }

        });

        imgBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        System.out.println(url);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            // Remember to set the bitmap in the main thread.
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    cameraPhoto.setImageBitmap(bitmap);
                                }
                            });
                        }else {
                            //Handle the error
                        }
                    }
                });
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start recording
                startRecording();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                // You may want to disable functionality that requires the permission
                Log.e("MainActivity", "RECORD_AUDIO permission denied");
            }
        }


    }

    private void startRecording() {
        // Initialize MediaRecorder and start recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(file);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Failed to start recording: " + e.getMessage());
        }
    }

    private void intiView() {
        btnProfile = findViewById(R.id.btnProfile);
        txtDoorStatus = findViewById(R.id.txtDoorStatus);
        imgBtnLock = findViewById(R.id.imgBtnLock);
        imgBtnSpeak = findViewById(R.id.imgBtnSpeak);
        imgBtnCamera = findViewById(R.id.imgBtnTakePhoto);
        cameraPhoto = findViewById(R.id.cameraPhoto);
        doorClosed = true;
        speakable = false;
        client = new OkHttpClient();
    }

}
