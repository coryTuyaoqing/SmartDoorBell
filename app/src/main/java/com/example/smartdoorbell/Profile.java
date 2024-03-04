package com.example.smartdoorbell;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    private TextView txtFirstName, txtSecondName, txtEmail;
    Button btnRegister;
    EditText edtTxtFirstName, edtTxtSecondName, edtTxtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtFirstName.setText(edtTxtFirstName.getText().toString());
                txtSecondName.setText(edtTxtSecondName.getText().toString());
                txtEmail.setText(edtTxtEmail.getText().toString());
            }
        });
    }

    private void initView() {
        txtFirstName = findViewById(R.id.txtFirstName);
        txtSecondName = findViewById(R.id.txtSecondName);
        txtEmail = findViewById(R.id.txtEmail);
        btnRegister = findViewById(R.id.btnRegist);
        edtTxtFirstName = findViewById(R.id.edtTxtFirstName);
        edtTxtSecondName = findViewById(R.id.edtTxtSecondName);
        edtTxtEmail = findViewById(R.id.edtTxtEmail);
    }


}