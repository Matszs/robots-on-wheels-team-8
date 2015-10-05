package com.example.suzanne.compass1;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class OverlayCompass extends AppCompatActivity {

    // Defines the image used as compass
    private ImageView image;

    // records the angle of rotation
    private float currentDegree = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay_compass);

        this.onSensorChanged();
    }

    public void onSensorChanged() {

        float degree = Math.round(180);


        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation turn = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        turn.setDuration(210);

        // set the animation after the end of the reservation status
        turn.setFillAfter(true);

        // Start the animation
        image.startAnimation(turn);
        currentDegree = -degree;



    }

    }



