package com.example.smartdoorbell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnProfile;
    private TextView txtDoorStatus;
    private ImageView cameraPhoto;

    private ImageButton imgBtnLock, imgBtnSpeak, imgBtnCamera;
    private boolean doorClosed, speakable;
    private OkHttpClient client;
    final private String url = "http://192.168.1.103/photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                }
                else{
                    imgBtnSpeak.setImageResource(R.drawable.ic_speak);
                    speakable = true;
                }
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