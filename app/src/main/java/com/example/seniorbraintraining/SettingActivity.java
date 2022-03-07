package com.example.seniorbraintraining;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class SettingActivity extends AppCompatActivity{


    private TextView aboutApp;
    private Button changePhoto;
    private ImageView profileImageView;
    private Button submit, play, stop;
    private EditText changeName;
    private EditText changePassword;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;

    private TextToSpeech textToSpeech;

    private static final int PERMISSION_CODE = 100;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private Uri uriImage;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        aboutApp = findViewById(R.id.aboutMe);
        changePhoto = findViewById(R.id.changePhotoID);
        profileImageView = findViewById(R.id.profileImageView);
        submit = findViewById(R.id.submit);
        changeName = findViewById(R.id.changeName);
        changePassword = findViewById(R.id.changePassword);
        play = findViewById(R.id.playSong);
        stop = findViewById(R.id.stopSong);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        StorageReference profileReference = storage.getReference().child("profile/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");

        profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });


        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmallow, request runtime
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                        //Permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        //Show popup to request permission;
                        requestPermissions(permission, PERMISSION_CODE);
                    }else{
                        //permission already granted
                        openCamera();
                    }
                }else{
                    //system os < marshmallow
                    openCamera();
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
                changePassword();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });



        aboutApp.setOnClickListener(View ->{
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, AboutAppActivity.class));
            overridePendingTransition(0, 0);

        });


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    //Select Langauge
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });


        play.setOnClickListener(View ->{
            playSong();
            play.setBackgroundColor(Color.parseColor("#FF3700B3"));
            stop.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        });

        stop.setOnClickListener(View ->{
            stopSong();
            play.setBackgroundColor(Color.parseColor("#FF03DAC5"));
            stop.setBackgroundColor(Color.parseColor("#FF3700B3"));
        });

    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        uriImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);

    }

    // Handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //This method is colled, when user presses allow or deny from permission request popup

        switch (requestCode){
            case PERMISSION_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission from popup was granted
                    openCamera();
                }else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //called when image was captured from camera

        if(resultCode == RESULT_OK){
            //Se the image captured to our image view
            //profileImageView.setImageURI(uriImage);

            uploadPicture();
        }
    }

    private void uploadPicture() {



        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        StorageReference pictureRef = storageRef.child("profile/"+mAuth.getCurrentUser().getUid() +"/profile.jpg");

        pictureRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(R.id.profileImageView), "Image uploaded", Snackbar.LENGTH_LONG).show();

                ////////no blink after recreate()
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull  UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });
    }


    public void changeName(){
        String newName = changeName.getText().toString().trim();

        if(newName.length()>=2 && newName.length()<=12){
            reference.child(user.getUid()).child("name").setValue(newName.toUpperCase());
            Toast.makeText(this, "Name changed successfully", Toast.LENGTH_LONG).show();
            textToSpeech.speak("Name changed successfully", TextToSpeech.QUEUE_FLUSH, null);
        }else {
            Toast.makeText(this, "Name length must be between 2 and 12", Toast.LENGTH_SHORT).show();
            textToSpeech.speak("Name length must be between 2 and 12", TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    public void changePassword(){
        String newPassword = changePassword.getText().toString().trim();

        if(newPassword.length() > 5){
            reference.child(user.getUid()).child("password").setValue(newPassword);
            changePassword.setText("");
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_LONG).show();
        }else {
            //Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show();
        }

    }



    public void playSong(){
        mediaPlayer = new MediaPlayer();
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {

            mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/seniorbraintraining.appspot.com/o/speck_-_Patchwork_Built.mp3?alt=media&token=6591aaf1-1b31-4b90-bc5e-9e27a315298e");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);

            Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();

        }
    }


    public void stopSong(){

        try {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}