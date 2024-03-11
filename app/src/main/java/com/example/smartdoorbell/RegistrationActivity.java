package com.example.smartdoorbell;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationActivity extends AppCompatActivity {
    OkHttpClient client;
    private TextView txtFirstName, txtSecondName, txtEmail, registrationStatus;
    Button btnRegister, btnLogin;
    EditText edtTxtFirstName, edtTxtSecondName, edtTxtEmail, edtPassWord, edtPassWordAgain;

    String postURL = "https://studev.groept.be/api/a23ib2a04/Registration_POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        client = new OkHttpClient();
        initView();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = edtTxtFirstName.getText().toString();
                String lName = edtTxtSecondName.getText().toString();
                String email = edtTxtEmail.getText().toString();
                String password = edtPassWord.getText().toString();
                String passwordagain = edtPassWordAgain.getText().toString();
                String errorMsg = "Passwords do not match";

                if (!fName.isEmpty() && !lName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordagain.isEmpty() && passwordagain.equals(password)) {
                    // Update TextViews with the entered data
                    txtFirstName.setText(fName);
                    txtSecondName.setText(lName);
                    txtEmail.setText(email);

                    RequestBody requestBody = new FormBody.Builder()
                            .add("fn", fName)
                            .add("ln", lName)
                            .add("mail", email)
                            .add("pw",password)
                            .build();

                    Request request = new Request.Builder()
                            .url(postURL)
                            .post(requestBody)
                            .build();

                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                            System.out.println(postURL);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            if (!response.isSuccessful()) {
                                System.out.println("Unsuccessful");
                            }
                        }
                    });


                } else {
                    if (!passwordagain.equals(password)) {
                        registrationStatus.setText(errorMsg);
                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initView() {
        txtFirstName = findViewById(R.id.txtFirstName);
        txtSecondName = findViewById(R.id.txtSecondName);
        txtEmail = findViewById(R.id.txtEmail);
        btnRegister = findViewById(R.id.btnRegist);
        btnLogin = findViewById(R.id.btnLogin);
        edtTxtFirstName = findViewById(R.id.edtTxtFirstName);
        edtTxtSecondName = findViewById(R.id.edtTxtSecondName);
        edtTxtEmail = findViewById(R.id.edtTxtEmail);
        edtPassWord = findViewById(R.id.edtPassWord);
        edtPassWordAgain = findViewById(R.id.edtPassWordAgain);
        registrationStatus = findViewById(R.id.registrationStatus); // Initialize registrationStatus TextView
    }
}
