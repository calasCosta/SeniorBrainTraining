package com.example.seniorbraintraining;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity  {

    private Button setting;
    private Button quickGame;
    private TextView userName, levelTextView;
    private ImageView logOutButton, profileImageView;


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private MediaPlayer mediaPlayer;

    private ImageView level1, level2, level3, level4, level5, level6, level7, level8, level9;

    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = findViewById(R.id.settingButton);
        quickGame = findViewById(R.id.quickGame);
        logOutButton = findViewById(R.id.logOutButton);
        setting = findViewById(R.id.settingButton);
        userName = findViewById(R.id.userName);
        levelTextView = findViewById(R.id.textViewLevel);
        profileImageView = findViewById(R.id.profileImageView);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        currentLevel = 1;



        ////////////////////////////

        level1 = findViewById(R.id.level1);
        level2 = findViewById(R.id.level2);
        level3 = findViewById(R.id.level3);
        level4 = findViewById(R.id.level4);
        level5 = findViewById(R.id.level5);
        level6 = findViewById(R.id.level6);
        level7 = findViewById(R.id.level7);
        level8 = findViewById(R.id.level8);
        level9 = findViewById(R.id.level9);

        ////////////////////////////


        mAuth = FirebaseAuth.getInstance();
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

        ////////////////////////////////
        //playSong();

        /////////////////////////////////

        unlockLevel();


        logOutButton.setOnClickListener(View ->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });


        setting.setOnClickListener(View -> {
           // playSong();
            startActivity(new Intent(this, SettingActivity.class));
        });

        quickGame.setOnClickListener(View ->{
            startActivity(new Intent (this, QuickGameActivity.class));
        });



        /////////////////////////////////////////

        unlockLevel();

        level1.setOnClickListener(View ->{
                startActivity(new Intent(this, Level1Activity.class));
        });

        level2.setOnClickListener(View ->{
            if(currentLevel >= 2)
                startActivity(new Intent(this, Level2Activity.class));
        });

        level3.setOnClickListener(View ->{
            if(currentLevel >= 3)
                startActivity(new Intent(this, Level3Activity.class));

        });

        level4.setOnClickListener(View ->{
            if(currentLevel >= 4)
                startActivity(new Intent(this, Level4Activity.class));
        });

        level5.setOnClickListener(View ->{
            if(currentLevel >= 5)
            startActivity(new Intent(this, Level5Activity.class));
        });

        level6.setOnClickListener(View ->{
            if(currentLevel >= 6)
            startActivity(new Intent(this, Level6Activity.class));
        });

        level7.setOnClickListener(View ->{
            if(currentLevel >= 7)
            startActivity(new Intent(this, Level7Activity.class));
        });

        level8.setOnClickListener(View ->{
            if(currentLevel >= 8)
            startActivity(new Intent(this, Level8Activity.class));
        });

        level9.setOnClickListener(View ->{
            if(currentLevel == 9)
            startActivity(new Intent(this, Level9Activity.class));
        });
        ///////////////////////////////////////
    }

    public void unlockLevel() {

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    userName.setText(userProfile.getName());
                    currentLevel = userProfile.getUnlockedLevels();
                    levelTextView.setText("Level "+ currentLevel);

                    if(currentLevel >= 2)
                        level2.setImageResource(R.drawable.ic_baseline_star_24);
                    if (currentLevel >= 3)
                        level3.setImageResource(R.drawable.ic_baseline_star_24);
                    if (currentLevel >= 4)
                        level4.setImageResource(R.drawable.ic_baseline_star_24);
                    if (currentLevel >= 5)
                        level5.setImageResource(R.drawable.ic_baseline_star_24);
                    if (currentLevel >= 6)
                        level6.setImageResource(R.drawable.ic_baseline_star_24);
                    if (currentLevel >= 7)
                        level7.setImageResource(R.drawable.ic_baseline_star_24);
                    if (currentLevel >= 8)
                        level8.setImageResource(R.drawable.ic_baseline_star_24);
                    if(currentLevel == 9)
                        level9.setImageResource(R.drawable.ic_baseline_star_24);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                //
            }
        });



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

            Toast.makeText(this, "KKKKKKKKKKKKKKKKKKKKKKK", Toast.LENGTH_LONG).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Toast.LENGTH_LONG).show();
        }
    }


    public void stopSong(){
        mediaPlayer.stop();
    }
}