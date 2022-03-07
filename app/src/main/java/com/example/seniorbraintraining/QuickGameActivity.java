package com.example.seniorbraintraining;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class QuickGameActivity extends AppCompatActivity implements View.OnClickListener {


    private int numberOfElements;
    private MemoryButton[] buttons;

    private int[] buttonGraphicLocations;
    private int[] buttonGraphics;

    private MemoryButton selectedButton1;

    private MemoryButton selectedButton2;

    private boolean isBusy = false;

    private TextView triesTextView;
    private TextView scoreTextView;

    private int numberOfTries;
    private int score;
    private boolean hit; //to verify match

    private final int MAX_NUMBER_OF_HIT = 8;
    private int currentNumberOfMatches;
    private boolean levelDone;


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference reference;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private ImageView backHome, refresh;
    private ConstraintLayout congratulationLayoutID;
    private TextView congratulationScore, congratulationTries;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level5);

        //GridLayout gridLayout = findViewById(R.id.gridLayout);

        androidx.gridlayout.widget.GridLayout gridLayout = findViewById(R.id.gridLayout);

        int numberOfColumns = gridLayout.getColumnCount();
        int numberOfRows = gridLayout.getRowCount();


        triesTextView = findViewById(R.id.tries);
        scoreTextView = findViewById(R.id.score);

        ////////////////////////////////////////////////////////////////////////////////////////////
        backHome = findViewById(R.id.backHome);
        refresh = findViewById(R.id.refresh);
        congratulationLayoutID = findViewById(R.id.congratulationContainerID);
        congratulationScore = findViewById(R.id.scoreCongratulation);
        congratulationTries = findViewById(R.id.triesCongratulation);
        ////////////////////////////////////////////////////////////////////////////////////////////

        numberOfElements = numberOfColumns * numberOfRows;

        buttons = new MemoryButton[numberOfElements];

        buttonGraphics = new int[numberOfElements / 2];

        numberOfTries = 0;
        currentNumberOfMatches = 0;
        score = 0;
        hit = false;  //variable to verify if the player catch two similar images

        buttonGraphics[0] = R.drawable.image_1;
        buttonGraphics[1] = R.drawable.image_2;
        buttonGraphics[2] = R.drawable.image_3;
        buttonGraphics[3] = R.drawable.image_4;
        buttonGraphics[4] = R.drawable.image_5;
        buttonGraphics[5] = R.drawable.image_6;
        buttonGraphics[6] = R.drawable.image_7;
        buttonGraphics[7] = R.drawable.image_8;

        buttonGraphicLocations = new int[numberOfElements];

        shuffleButtonGraphics();

        for (int r = 0; r < numberOfRows; r++) {
            for (int c = 0; c < numberOfColumns; c++) {
                MemoryButton tempButton = new MemoryButton(this, r, c, buttonGraphics[buttonGraphicLocations[r * numberOfColumns + c]]);

                tempButton.setId(View.generateViewId());
                tempButton.setOnClickListener(this);
                buttons[r * numberOfColumns + c] = tempButton;
                gridLayout.addView(tempButton);
            }
        }


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");


        ///////////////////////////////////////////////////////////////////////////////////////////////
        backHome.setOnClickListener(View ->{
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
        });

        refresh.setOnClickListener(View ->{
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            congratulationLayoutID.setVisibility(View.INVISIBLE);
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public boolean isLevelDone() {
        return levelDone;
    }

    public void setLevelDone(boolean levelDone) {
        this.levelDone = levelDone;
    }

    private void shuffleButtonGraphics() {
        Random random = new Random();

        for (int i = 0; i < numberOfElements; i++) {
            buttonGraphicLocations[i] = i % (numberOfElements / 2);
        }

        for (int i = 0; i < numberOfElements; i++) {
            int temp = buttonGraphicLocations[i];

            int swapIndex = random.nextInt(4*4);

            buttonGraphicLocations[i] = buttonGraphicLocations[swapIndex];

            buttonGraphicLocations[swapIndex] = temp;
        }
    }

    @Override
    public void onClick(View v) {


        if (isBusy)
            return;

        MemoryButton button = (MemoryButton) v;

        if (button.isMatched())
            return;


        if (selectedButton1 == null) {
            selectedButton1 = button;
            selectedButton1.flip();
            return;
        }

        if (selectedButton1.getId() == button.getId()) {
            return;
        }

        if (selectedButton1.getFrontDrawableID() == button.getFrontDrawableID()) {
            numberOfTries++;
            currentNumberOfMatches++;

            triesTextView.setText("" + numberOfTries);

            if (numberOfTries < 5 || hit) {
                score += 100;
            } else {
                score += 20;
            }

            if (currentNumberOfMatches == MAX_NUMBER_OF_HIT){


                /////////////////////////////////////////////////////////////////////////////////////////////////////////////

                congratulationTries.setText(triesTextView.getText().toString());
                congratulationScore.setText(scoreTextView.getText().toString());
                congratulationLayoutID.setVisibility(View.VISIBLE);

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////
                userID = user.getUid();

            }

            scoreTextView.setText("" + score);

            button.flip();

            button.setMatched(true);
            selectedButton1.setMatched(true);

            selectedButton1.setEnabled(false);
            button.setEnabled(false);

            selectedButton1 = null;
            hit = true;




            return;

        } else {

            numberOfTries++;
            triesTextView.setText("" + numberOfTries);
            hit = false;

            selectedButton2 = button;
            selectedButton2.flip();
            isBusy = true;

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedButton2.flip();
                    selectedButton1.flip();
                    selectedButton1 = null;
                    selectedButton2 = null;

                    isBusy = false;
                }
            }, 500);

        }

    }
}