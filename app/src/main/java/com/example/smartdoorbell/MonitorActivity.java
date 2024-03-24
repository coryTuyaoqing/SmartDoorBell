package com.example.smartdoorbell;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import io.socket.client.Socket;

public class MonitorActivity extends AppCompatActivity {
    private Socket socket;
    private ImageView imageView;
    private Button btnMonitorBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

//        imageView = findViewById(R.id.imgMonitor);
//        btnMonitorBackHome = findViewById(R.id.btnMonitorBakcHome);
//        socket = SocketManager.getInstance(MainActivity.getIp());
//
//        socket.emit("monitor_request", "android clinet request for door bell monitor");
//
//        socket.on("video_chunk", new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                // Process received video frame
//                byte[] data = (byte[]) args[0];
//                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//                // Update UI on the main thread
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        imageView.setImageBitmap(bitmap);
//                    }
//                });
//            }
//        });
//
//        socket.connect();
//
//        btnMonitorBackHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MonitorActivity.this, MainActivity.class));
//            }
//        });
    }
//
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        socket.disconnect();
    }
}