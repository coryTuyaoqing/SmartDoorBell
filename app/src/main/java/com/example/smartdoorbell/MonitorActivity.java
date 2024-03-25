package com.example.smartdoorbell;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;

public class MonitorActivity extends AppCompatActivity {
    private OkHttpClient okHttpClient;
    private ImageView imageView;
    private Button btnMonitorBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

//        imageView = findViewById(R.id.imgMonitor);
//        btnMonitorBackHome = findViewById(R.id.btnMonitorBakcHome);
//        okHttpClient = new OkHttpClient();
//
//        //Build the request with the URL and request body
//        Request request = new Request.Builder()
//                .url("http://192.168.0.110/monitor")
//                //.post(requestBody)
//                .get()
//                .build();
//
//        //Initiate asynchronous HTTP request with okhttp
//        //enqueue method performs the request asynchronously + expects callback to handle the response.
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            //@NonNull indicates that the field cannot contain null value
//            //the compiler or analysis tool may generate a warning or error if the value is null
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
//                Log.e("MonitorActicity", "Failed to send boolean value to server: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    // Process received video frame
//                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
//                    //Remember to set the bitmap in the main thread.
//                    //Schedule a task to be executed on the main UI thread using Handler
//                    //Looper.getMainLooper() is responsible for running tasks on the main thread
//                    //Method runnable has run()
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            imageView.setImageBitmap(bitmap);
//                            //Sets the Bitmap (bitmap) to be displayed in the ImageView (cameraPhoto) using cameraPhoto.setImageBitmap(bitmap).
//                        }
//                    });
//                }else {
//                    //Handle the error
//                }
//            }
//
//        });
//
//        btnMonitorBackHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MonitorActivity.this, MainActivity.class));
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}