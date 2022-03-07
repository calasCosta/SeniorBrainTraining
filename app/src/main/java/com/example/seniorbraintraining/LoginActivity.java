package com.example.seniorbraintraining;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView register, forgetPassword;

    private ImageButton microphoneEmail;
    private TextToSpeech textToSpeech;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_login);





        emailEditText = findViewById(R.id.editTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        register = findViewById(R.id.register);
        forgetPassword = findViewById(R.id.forgetPassword);
        microphoneEmail = findViewById(R.id.microphoneEmail);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(View ->{
            loginUser();
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


        register.setOnClickListener(View -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(0, 0);

        });


        // reset the password
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your Email to receive reset link ");
                passwordResetDialog.setView(resetEmail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Extract the email and send reset link
                        String email = resetEmail.getText().toString();
                        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show();
                                textToSpeech.speak("Reset link sent to your email.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error, Reset link was not send." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                textToSpeech.speak("Error, Reset link was not send.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });


        microphoneEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }

    private void speak() {
        // Intent to show speech to text dialog
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, say your email");

        // This starts the activity and populates the intent with the speech text.

        //startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

                emailActivityResultLauncher.launch(intent);

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




    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(email)){
            emailEditText.setError("Email is required!");
            textToSpeech.speak("Email is required!", TextToSpeech.QUEUE_FLUSH, null);
            emailEditText.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            passwordEditText.setError("Password cannot be empty.");
            textToSpeech.speak("Password is required!", TextToSpeech.QUEUE_FLUSH, null);
            passwordEditText.requestFocus();
        }else{

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@Nullable Task<AuthResult> task){
                    if(task.isSuccessful()){
                        //Toast.makeText(LoginActivity.this, "User logged-in successfully.", Toast.LENGTH_SHORT).show();
                        //textToSpeech.speak("User logged-in successfully.", TextToSpeech.QUEUE_FLUSH, null);

                        overridePendingTransition(0, 0);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);

                    }else{
                        Toast.makeText(LoginActivity.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Check if user is signed in (non - null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

    }

}