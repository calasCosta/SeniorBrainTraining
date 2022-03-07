package com.example.seniorbraintraining;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.GridLayout;

import static androidx.core.content.ContextCompat.getDrawable;

public class MemoryButton extends androidx.appcompat.widget.AppCompatButton {

    private int row;
    private int column;
    private int frontDrawableID;
    private boolean isFlipped = false;
    private boolean isMatched = false;

    private Drawable front;
    private Drawable back;


    public MemoryButton(Context context, int row, int column, int frontImageDrawableID){
        super(context);

        this.row = row;
        this.column = column;
        this.frontDrawableID = frontImageDrawableID;


        front = getDrawable(context, frontImageDrawableID);
        back = getDrawable(context, R.drawable.question_mark_img);



        setBackground(back);

        GridLayout.LayoutParams tempParams = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column));

        tempParams.width = (int) getResources().getDisplayMetrics().density * 110;
        tempParams.height = (int) getResources().getDisplayMetrics().density * 150;


        setLayoutParams(tempParams);
    }


    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public int getFrontDrawableID() {
        return frontDrawableID;
    }

    public void flip(){
        if(isMatched)
            return;

        if (isFlipped){
            setBackground(back);
            isFlipped = false;
        }else{
            setBackground(front);
            isFlipped = true;
        }
    }
}
