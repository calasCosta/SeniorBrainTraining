package com.example.seniorbraintraining;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private TextView login;

    private EditText userName;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmationEditText;
    private Button registerButton;
    private ImageButton microphoneEmail;
    private ImageButton microphoneName;
    private TextView textViewLogin;

    private TextToSpeech textToSpeech;
    private User user;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        login = findViewById(R.id.register);
        userName = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordConfirmationEditText = findViewById(R.id.editTextPassword2);
        microphoneEmail = findViewById(R.id.microphoneImageButtonEmail);
        microphoneName = findViewById(R.id.microphoneImageButtonName);
        registerButton = findViewById(R.id.loginButton);
        textViewLogin = findViewById(R.id.register);
        registerButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    //Select Language
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        registerButton.setOnClickListener(View ->{
            createUser();
        });

        login.setOnClickListener(View ->{
            startActivity(new Intent(this, LoginActivity.class));
        });

        microphoneEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(v);
            }
        });

        microphoneName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(v);
            }
        });
    }

    /************************ microphone start ****************************/

    private void speak(View v) {
        // Intent to show speech to text dialog
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, start recording!");


        // This starts the activity and populates the intent with the speech text.

        //startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        switch (v.getId()){
            case R.id.microphoneImageButtonEmail:
                emailActivityResultLauncher.launch(intent);
                break;
            case R.id.microphoneImageButtonName:
                nameActivityResultLauncher.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> emailActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        ArrayList<String > results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        //set to nameEditText

                        emailEditText.setText(results.get(0));
                    }
                }
            });

    ActivityResultLauncher<Intent> nameActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        ArrayList<String > results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        //set to nameEditText

                        userName.setText(results.get(0));
                    }
                }
            });
    /************************ microphone end ****************************/

    private void createUser() {
        String name = userName.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirmation = passwordConfirmationEditText.getText().toString().trim();



        if(name.isEmpty()){
            userName.setError("Name is required!");
            textToSpeech.speak("Name is required!", TextToSpeech.QUEUE_FLUSH, null);
            userName.requestFocus();
            return;
        }

        if(name.length() > 12){
            userName.setError("Name length can't be more than 12!");
            textToSpeech.speak("Name length can't be more than 12!", TextToSpeech.QUEUE_FLUSH, null);
            userName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailEditText.setError("Email is required!");
            textToSpeech.speak("Email is required!", TextToSpeech.QUEUE_FLUSH, null);
            emailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            passwordEditText.setError("Password is required!");
            textToSpeech.speak("Password is required!", TextToSpeech.QUEUE_FLUSH, null);
            passwordEditText.requestFocus();
            return;
        }
        if(passwordConfirmation.isEmpty()){
            passwordConfirmationEditText.setError("Password is required!");
            textToSpeech.speak("Password is required!", TextToSpeech.QUEUE_FLUSH, null);
            passwordConfirmationEditText.requestFocus();
            return;
        }
        if(password.length() <6){
            passwordEditText.setError("Password should contain at least 6 characters!");
            textToSpeech.speak("Password should contain at least 6 characters!", TextToSpeech.QUEUE_FLUSH, null);
            passwordEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide valid email");
            textToSpeech.speak("Please provide valid email", TextToSpeech.QUEUE_FLUSH, null);
            emailEditText.requestFocus();
            return;
        }
            if(password.equals(passwordConfirmation) ){
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@Nullable Task<AuthResult> task){
                        if(task.isSuccessful()){
                            user = new User(name.toUpperCase(), email, password);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            database.getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_LONG).show();
                                        textToSpeech.speak("User registered successfully!", TextToSpeech.QUEUE_FLUSH, null);
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    }
                                }
                            });



                        }else{
                            Toast.makeText(RegisterActivity.this, "Registration Fails!: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            textToSpeech.speak("Registration Fails!", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });
            }else{
                Toast.makeText(this, "Both passwords must be equals!", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("Both passwords must be equals!", TextToSpeech.QUEUE_FLUSH, null);
            }
    }

}