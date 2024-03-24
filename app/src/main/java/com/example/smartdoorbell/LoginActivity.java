package com.example.smartdoorbell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    OkHttpClient client;
    EditText edtEmail, edtPassword;
    Button btnLogin2, btnSignUp;
    String getURL = "https://studev.groept.be/api/a23ib2a04/Login";
    String errorMsg2 = "Login unsuccessful";
    String errorMsg3 = "Fill in all fields";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client = new OkHttpClient();
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin2 = findViewById(R.id.btnLogin2);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()){
                    RequestBody requestBody = new FormBody.Builder()
                            .add("mail", email)
                            .build();

                    Request request = new Request.Builder()
                            .url(getURL)
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                            System.out.println(getURL);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseData = response.body().string();
                            try {
                                JSONArray jsonArray = new JSONArray(responseData);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String dbPassword = jsonObject.optString("Password");
                                String dbID = jsonObject.optString("idRegistration");
                                String dbFirstName = jsonObject.optString("FirstName");
                                String dbLastName = jsonObject.optString("LastName");
                                String dbEmail = jsonObject.optString("Email");
                                if (dbPassword.equals(password)) {
                                    System.out.println(password);

                                    UserInfo userInfo = new UserInfo(getApplicationContext());
                                    userInfo.initInfo();
                                    userInfo.writeInfo(dbFirstName,dbLastName,dbEmail);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, errorMsg2, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                } else{
                    Toast.makeText(LoginActivity.this, errorMsg3, Toast.LENGTH_LONG).show();
                }
            }
        });

     btnSignUp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
             startActivity(intent);
         }
     });
    }
}
