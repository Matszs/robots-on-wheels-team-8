package com.example.robotsonwheels;

import android.app.ActionBar;
import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Robot extends AppCompatActivity {

    private final static int START_DRAGGING = 0;
    private final static int STOP_DRAGGING = 0;

    private ImageView images;
    private  RelativeLayout layout;
    private int status;
    private RelativeLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
        layout = (RelativeLayout) findViewById(R.id.ROW);
        images = (ImageView) findViewById(R.id.joystick);
        images.setDrawingCacheEnabled(true);
        params = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        final TextView coordinates = (TextView) findViewById(R.id.textView);

        final RelativeLayout joystickHolder = (RelativeLayout)findViewById(R.id.joystickHolder);

        joystickHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)joystickHolder.getLayoutParams();
                SocketClient sender = new SocketClient();
                System.out.println("TEST");
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    status = START_DRAGGING;
                }
                else if(event.getAction()==MotionEvent.ACTION_UP){
                    status = STOP_DRAGGING;
                    layoutParams.setMargins(200,300, 0, 0);
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE){
                    if (status == START_DRAGGING){
                        System.out.print("Dragging");
                        if((int)event.getX() <= 400 && (int)event.getY() >= 100 && (int) event.getY() <= 500) {
                            if(cirkelWithPitagoras((int)event.getX(),(int)event.getY())) {
                                layoutParams.setMargins((int) event.getX(), (int) event.getY(), 0, 0);
                                coordinates.setText(Integer.toString((int) event.getX()) + " " + Integer.toString((int) event.getY()));
                                joystickHolder.setLayoutParams(layoutParams);
                                if((int)event.getX() < 200){
                                    try{
                                        sender.write(200 - (int)event.getX());
                                    }
                                    catch (Exception ex){

                                    }
                                }
                                else if((int)event.getX() > 200){
                                    try {
                                        sender.write((int) event.getX() - 200);
                                    }
                                    catch (Exception ex){

                                    }
                                }
                                if((int)event.getY() < 300){
                                    try{
                                        sender.write(200 - (int)event.getY());
                                    }
                                    catch (Exception ex){

                                    }
                                }
                                else if((int)event.getY() > 300){
                                    try {
                                        sender.write((int) event.getY() - 200);
                                    }
                                    catch (Exception ex){

                                    }
                                }

                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_robot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean cirkelWithPitagoras(int x, int y){
        int tmpxcor = x * x;
        int tmpycor = y * y;
        double pyt = Math.sqrt(tmpxcor + tmpycor);
        if (pyt <= 150){
            System.out.print("FALSE");
            return false;
        }
        else {
            System.out.print("TRUE");
            return true;
        }
    }
}
