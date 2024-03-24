package com.example.smartdoorbell;

import android.Manifest;
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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.socket.client.Socket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity {

    private Button btnProfile, btnToMonitor;
    private TextView txtDoorStatus, txtStatus;
    private VideoView videoView;
    private ImageView cameraPhoto;

    private MediaRecorder mediaRecorder;
    public static String fileName = "recorded.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;

    private ImageButton imgBtnLock, imgBtnSpeak, imgBtnCamera;
    private boolean doorClosed, speakable;
    private OkHttpClient client;
    private static final String ip = "http://192.168.0.110";
    final private String url = ip + "/photo";
    private final String url_upload_audio = ip + "/upload_audio";
    private final String url_download_audio = ip + "/download_audio";
    private final String url_download_video = ip + "/download_video";
    private final String url_unlock = ip + "/unlock";
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 100;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // If the permission for storage usage was already granted, nothing should happen
            } else {
                //Otherwise an intent is opened to manage settings for app permissions
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        //Check if android version is >6.0 and if record audio permission has NOT been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //If the RECORD_AUDUO permission has NOT been granted, it must be requested
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQUEST_CODE);

        }


        UserInfo userInfo = new UserInfo(getApplicationContext());
        if(!userInfo.fileExist()){
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        }


        intiView();
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            //onClick listener, each btn has one of these, they all override each other
            public void onClick(View v) {
                Intent intent;
                //User info has to be stored so that you do not need to give audio recording permissions each time
                //Also stores login details (initializes application context)
                UserInfo userInfo = new UserInfo(getApplicationContext());
                if(!userInfo.fileExist()){
                    //Indicates that the user info has been initialised
                    intent  = new Intent(MainActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                }
                else{
                    //Indicates that the user info has not been initialised
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            }
        });

        imgBtnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtDoorStatus.getText().equals("CLOSE")){
                    imgBtnLock.setImageResource(R.drawable.ic_unlock);
                }
                //Convert boolean value to string representation
                String booleanString = String.valueOf(!doorClosed);

                //Create request body containing the boolean value
                RequestBody requestBody = new FormBody.Builder()
                        .add("doorClosed", booleanString)
                        .build();

                //Build the request with the URL and request body
                Request request = new Request.Builder()
                        .url(url_unlock)
                        //.post(requestBody)
                        .get()
                        .build();


                //Initiate asynchronous HTTP request with okhttp
                //enqueue method performs the request asynchronously + expects callback to handle the response.
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    //@NonNull indicates that the field cannot contain null value
                    //the compiler or analysis tool may generate a warning or error if the value is null
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        Log.e("MainActivity", "Failed to send boolean value to server: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            // Handle successful response from the server
                            Log.d("MainActivity", "Boolean value sent to server successfully");
                        } else {
                            // Handle unsuccessful response from the server
                            Log.e("MainActivity", "Failed to send boolean value to server: " + response.code());
                        }
                    }
                });
            }
        });


        imgBtnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speakable){
                    imgBtnSpeak.setImageResource(R.drawable.ic_deafen);
                    speakable = false;
                    //When speakable becomes false, indicating that the audio should not be recorded,
                    //stopAudio is called
                    stopAudio();
                    //Audio that was recorded is uploaded
                    uploadAudioFile(file);
                }
                else{
                    imgBtnSpeak.setImageResource(R.drawable.ic_speak);
                    speakable = true;
                    //When speakable becomes true then audio recording should start
                    startRecording();
                }
            }
        });


        imgBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // display video
                Request cameraRequest = new Request.Builder().url(url_download_video).build();
                client.newCall(cameraRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        System.out.println(url_download_video);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // Check whether or not the response was successful, indicating that the request was
                        // successfully received, understood, and accepted.
                        if (response.isSuccessful()) {
                            final InputStream inputStream = response.body().byteStream();
                            File tempVideoFile = File.createTempFile("temp_video", ".mp4", getCacheDir());
                            FileOutputStream outputStream = new FileOutputStream(tempVideoFile);

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            // Close streams
                            inputStream.close();
                            outputStream.close();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Set the video stream to the VideoView
                                    videoView.setVideoPath(tempVideoFile.getAbsolutePath());

                                    // Set up a MediaController
//                                    MediaController mediaController = new MediaController(MainActivity.this);
//                                    mediaController.setAnchorView(videoView);
//                                    videoView.setMediaController(mediaController);

                                    // Set up a listener to adjust layout parameters when prepared
                                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            // Start playing the video
                                            videoView.start();
                                        }
                                    });

                                    // Set up a listener to freeze last frame when playback completes
                                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            // Pause the video playback
                                            videoView.pause();
                                            // Seek to the end of the video to display the last frame
                                            videoView.seekTo(videoView.getDuration());
                                            videoView.setVisibility(View.INVISIBLE);
                                        }
                                    });

                                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                                            // Handle error
                                            Log.e("Video Playback", "Error: " + what + ", " + extra);
                                            return false; // Return true if the error is handled
                                        }
                                    });

                                    // Make VideoView visible
                                    videoView.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            // Handle the error
                        }
                    }
                });

                //display audio
                Request audioRequest = new Request.Builder().url(url_download_audio).build();
                client.newCall(audioRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        System.out.println(url_download_audio);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final InputStream inputStream = response.body().byteStream();
                            try {
                                // Create a temporary audio file
                                File tempAudioFile = File.createTempFile("temp_audio", ".wav", getCacheDir());
                                FileOutputStream outputStream = new FileOutputStream(tempAudioFile);

                                // Write audio data to the temporary file
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }

                                // Close streams
                                inputStream.close();
                                outputStream.close();

                                // Post a runnable to the main thread for playing audio
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // Create MediaPlayer and play audio
                                            MediaPlayer mediaPlayer = new MediaPlayer();
                                            mediaPlayer.setDataSource(tempAudioFile.getAbsolutePath());
                                            mediaPlayer.prepare();
                                            mediaPlayer.start();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Handle the error
                        }
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        runOnUiThread(() -> {
                            // Stop video playback after 10 seconds
                            videoView.stopPlayback();
                            // Make VideoView invisible
                            videoView.setVisibility(View.INVISIBLE);
                        });
                    }
                }).start();
            }
        });

//        btnToMonitor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, MonitorActivity.class));
//            }
//        });

        socket.on("message", args -> {
            String message = (String) args[0];
            switch (message) {
                case "Some one is at the door!":
                    System.out.println("Some one is at the door!");
                    new Thread(() -> {
                        try {
                            int t_sleep = 15000;
                            getCameraPhoto(t_sleep);
                            runOnUiThread(() -> txtStatus.setText(message));
                            Thread.sleep(t_sleep);
                            runOnUiThread(() -> txtStatus.setText(""));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    break;
                case "The door is opened":
                    System.out.println("The door is opened");
                    runOnUiThread(() -> {
                        txtDoorStatus.setText("OPEN");
                    });
                    break;
                case "The door is closed":
                    System.out.println("The door is closed");
                    runOnUiThread(() -> {
                        txtDoorStatus.setText("CLOSE");
                        imgBtnLock.setImageResource(R.drawable.ic_lock);
                    });
                    break;
                default:
                    System.out.println("incorrect message");
                    break;
            }
        });

        socket.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted by the user, recording can be started
                Log.d("MainActivity", "RECORD_AUDIO permission granted");
                //startRecording() IF IT IS BROKEN NOW ADD THIS BACK!!!!!!!
            } else {
                //Nothing can happen if this is blocked, the functionality will simply not work
                Log.e("MainActivity", "RECORD_AUDIO permission denied");
            }
        }
    }

    private void startRecording() {
        //Initialising mediarecorder
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
                .url(url_upload_audio)
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

    public void getCameraPhoto(int t_sleep){
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            //Called when the request could not be executed due to cancellation, a connectivity problem or timeout.
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                System.out.println(url);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Check whether or not the response was successful, indicating that the request was
                //successfully received, understood, and accepted.
                if (response.isSuccessful()){
                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    //Remember to set the bitmap in the main thread.
                    //Schedule a task to be executed on the main UI thread using Handler
                    //Looper.getMainLooper() is responsible for running tasks on the main thread
                    //Method runnable has run()
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            cameraPhoto.setImageBitmap(bitmap);
                            //Sets the Bitmap (bitmap) to be displayed in the ImageView (cameraPhoto) using cameraPhoto.setImageBitmap(bitmap).
                        }
                    });
                }else {
                    //Handle the error
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(t_sleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(() -> cameraPhoto.setImageResource(R.drawable.ic_doorway_spot));
            }
        }).start();
    }

    public static String getIp(){
        return ip;
    }

    private void intiView() {
        btnProfile = findViewById(R.id.btnProfile);
//        btnToMonitor = findViewById(R.id.btnToMonitor);
        txtDoorStatus = findViewById(R.id.txtDoorStatus);
        txtStatus = findViewById(R.id.txtStatus);
        imgBtnLock = findViewById(R.id.imgBtnLock);
        imgBtnSpeak = findViewById(R.id.imgBtnSpeak);
        imgBtnCamera = findViewById(R.id.imgBtnTakePhoto);
        videoView = findViewById(R.id.videoView);
        cameraPhoto = findViewById(R.id.cameraPhoto);
        doorClosed = true;
        speakable = false;
        client = new OkHttpClient();
        socket = SocketManager.getInstance(ip);
    }
}
