package com.example.smartdoorbell;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                String errorMsg2 = "Please fill in all fields";

                if (!fName.isEmpty() && !lName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordagain.isEmpty() && passwordagain.equals(password)) {
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

                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
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
                        Toast.makeText(RegistrationActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(RegistrationActivity.this, errorMsg2, Toast.LENGTH_LONG).show();
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
        btnRegister = findViewById(R.id.btnRegist);
        btnLogin = findViewById(R.id.btnLogin);
        edtTxtFirstName = findViewById(R.id.edtTxtFirstName);
        edtTxtSecondName = findViewById(R.id.edtTxtSecondName);
        edtTxtEmail = findViewById(R.id.edtTxtEmail);
        edtPassWord = findViewById(R.id.edtPassWord);
        edtPassWordAgain = findViewById(R.id.edtPassWordAgain); // Initialize registrationStatus TextView
    }
}
