package com.example.smartdoorbell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    Button btnProfileHome, btnProfileLogout;
    TextView txtFirstName, txtLastName, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();

        initProfile();
        btnProfileHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });
    }

    public void initView(){
        btnProfileHome = findViewById(R.id.btnProfileHome);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);
        txtFirstName = findViewById(R.id.txtProfileFirstName);
        txtLastName = findViewById(R.id.txtProfileLastName);
        txtEmail = findViewById(R.id.txtProfileEmail);

    }

    public void initProfile(){
        UserInfo userInfo = new UserInfo(getApplicationContext());
        String[] info = userInfo.readFile();
        txtFirstName.setText(info[0]);
        txtLastName.setText(info[1]);
        txtEmail.setText(info[2]);
    }
}