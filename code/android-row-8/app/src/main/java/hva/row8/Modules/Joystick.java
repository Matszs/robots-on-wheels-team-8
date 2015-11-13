package hva.row8.Modules;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hva.row8.Classes.Calculation;
import hva.row8.Interfaces.DataReceiveListener;
import hva.row8.Interfaces.MoveListener;

/**
 * Created by Mats-Mac on 12-10-15.
 */
public class Joystick {
	private Activity activity;
	private RelativeLayout container;
	private RelativeLayout analog;
	private int radius = 0;
	private int xHolder;
	private int yHolder;
	private List<MoveListener> listeners = new ArrayList<MoveListener>();
	private boolean moveJoystick = false;

	private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
	private long lastClickTime = 0;

	public Joystick(Activity activity, RelativeLayout container, RelativeLayout analog) {
		this.container = container;
		this.analog = analog;
		this.activity = activity;

		RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) analog.getLayoutParams();

		Calculation.startX = containerParams.width / 2 - (layoutParams.width / 2); // set default location calculation
		Calculation.startY = containerParams.height / 2 - (layoutParams.height / 2); // set default location calculation
		//Calculation.radius = (layoutParams.height / 2); // radius in DP instead of pixels
		this.radius = Calculation.radius = (int) (layoutParams.height * activity.getResources().getDisplayMetrics().density + 0.5f) / 2; // radius in pixels instead of DP.

		SharedPreferences settings = activity.getSharedPreferences("JoyStickPref", 0);
		int x = settings.getInt("x", -1);

		if(x >= 0) {
			containerParams.leftMargin = x;
			container.setLayoutParams(containerParams);
		}

	}

	public void bind() {
		// Make container movable on double press
		container.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int cursorX = (int) event.getRawX();

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						if(moveJoystick) {
							xHolder = cursorX - layoutParams.leftMargin;
						}
						break;
					case MotionEvent.ACTION_UP:
						long clickTime = System.currentTimeMillis();
						if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA)
							moveJoystick = !moveJoystick;

						lastClickTime = clickTime;

						container.setAlpha((moveJoystick ? 0.5f : 1f));

						break;

					case MotionEvent.ACTION_MOVE:
						if(moveJoystick) {
							int newX = cursorX - xHolder;

							if (newX < 0)
								newX = 0;

							DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
							int width = displayMetrics.widthPixels;
							int height = displayMetrics.heightPixels;

							if(newX > (width - radius * 2))
								newX = (width - radius * 2);

							layoutParams.leftMargin = newX;

							container.setLayoutParams(layoutParams);

							SharedPreferences settings = activity.getSharedPreferences("JoyStickPref", 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putInt("x", newX);
							editor.commit();

						}
						break;
				}

				return true;
			}
		});

		analog.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(moveJoystick)
					return true;
				int cursorX = (int) event.getRawX();
				int cursorY = (int) event.getRawY();

				RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) analog.getLayoutParams();

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						xHolder = cursorX - layoutParams.leftMargin;
						yHolder = cursorY - layoutParams.topMargin;
						break;
					case MotionEvent.ACTION_UP:

						layoutParams.leftMargin = containerParams.width / 2 - (layoutParams.width / 2);
						layoutParams.topMargin = containerParams.height / 2 - (layoutParams.height / 2);

						analog.setLayoutParams(layoutParams);
						sendToListeners(layoutParams.leftMargin, layoutParams.topMargin);
						sendToListeners(layoutParams.leftMargin, layoutParams.topMargin);
						sendToListeners(layoutParams.leftMargin, layoutParams.topMargin);
						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						break;
					case MotionEvent.ACTION_POINTER_UP:
						break;
					case MotionEvent.ACTION_MOVE:
						int newX = cursorX - xHolder;
						int newY = cursorY - yHolder;

						if(newX < 0)
							newX = 0;
						if(newY < 0)
							newY = 0;

						if(newX > (containerParams.width - layoutParams.width))
							newX = (containerParams.width - layoutParams.width);
						if(newY > (containerParams.height - layoutParams.height))
							newY = (containerParams.height - layoutParams.height);

						layoutParams.leftMargin = newX;
						layoutParams.topMargin = newY;

						analog.setLayoutParams(layoutParams);
						sendToListeners(newX, newY);
						break;
				}

				return true;
			}
		});
	}

	private void sendToListeners(int x, int y) {
		for(MoveListener moveListener : listeners)
			moveListener.onMove(x, y);
	}

	public void addListener(MoveListener moveListener) {
		listeners.add(moveListener);
	}
}
