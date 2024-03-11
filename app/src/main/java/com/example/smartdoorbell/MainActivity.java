package com.example.smartdoorbell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnProfile;
    private TextView txtDoorStatus;

    private ImageButton imgBtnLock, imgBtnSpeak, imgBtnCamera;
    private boolean doorClosed, speakable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intiView();


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
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
    }

    private void intiView() {
        btnProfile = findViewById(R.id.btnProfile);
        txtDoorStatus = findViewById(R.id.txtDoorStatus);
        imgBtnLock = findViewById(R.id.imgBtnLock);
        imgBtnSpeak = findViewById(R.id.imgBtnSpeak);
        imgBtnCamera = findViewById(R.id.imgBtnTakePhoto);
        doorClosed = true;
        speakable = false;
    }

}