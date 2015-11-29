package hva.row8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import hva.row8.Classes.Calculation;
import hva.row8.Classes.Connection;
import hva.row8.Interfaces.DataReceiveListener;
import hva.row8.Interfaces.MoveListener;
import hva.row8.Modules.Joystick;
import hva.row8.Modules.SocketClient;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ConnectionActivity extends AppCompatActivity {
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final int UI_ANIMATION_DELAY = 300;

	private View mContentView;
	private RelativeLayout videoOverlay;
	public Application application;
	private Connection connection;
	private float lastCompassRotation = 0;
	SocketClient socketConnection;
	private boolean mVisible;
	private TextView status;
	private Button licensePlateButton;

	private RelativeLayout wallHitHolder;
    private boolean isActive = false;

	private final Handler mHideHandler = new Handler();
	private final Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			hide();
		}
	};

	private int xHolder;
	private int yHolder;

	private Joystick joystick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
			actionBar.hide();

		application = (Application)getApplication();

		Intent connectionIntent = getIntent();
		int connectionId = connectionIntent.getIntExtra("connection_id", 0);

		if(connectionId == 0) {
			Toast.makeText(ConnectionActivity.this, "Connection cannot be made. [connection_id not found]", Toast.LENGTH_SHORT).show();
			this.finish();
		}

		connection = application.connectionDataSource.getConnection(connectionId);
		mContentView = findViewById(R.id.connection_holder);
		videoOverlay = (RelativeLayout)findViewById(R.id.video_overlay);
		wallHitHolder = (RelativeLayout)findViewById(R.id.wall_hit_holder);
		status = (TextView)findViewById(R.id.status);
		CheckBox wallHit = (CheckBox)findViewById(R.id.wall_hit);

		writeStatus("Initialising...");

		mContentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggle();
			}
		});

		wallHit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				try {
					socketConnection.write(8, new byte[]{(byte) (isChecked ? 1 : 0)});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		licensePlateButton = (Button)findViewById(R.id.read_license_plate);
		licensePlateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					socketConnection.write(7, new byte[]{ });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		if(AUTO_HIDE)
			hide(); // Automatically hide the controls

		VideoView videoView = (VideoView)findViewById(R.id.video_view);
		//Uri UriSrc = Uri.parse("http://" + connection.ip + ":8090");
		Uri UriSrc = Uri.parse("rtsp://" + connection.ip + ":8554/unicast");
		videoView.setVideoURI(UriSrc);
		// Capture error message by a toast, more user friendly.
		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Toast.makeText(ConnectionActivity.this, "Cannot play video.", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				videoOverlay.setVisibility(View.INVISIBLE);
			}
		});
		videoView.start();

		joyStickInit();

		new Thread(new ClientThread()).start();
	}

    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }

    private void displayLicensePlateToast(byte[] textData) {

		int i;
		for (i = 0; i < textData.length && textData[i] != 0; i++);

		String text;
		try {
			text = new String(textData, 0, i, "us-ascii");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		final String textFormatted = text.replaceAll("\\s+","");

		if(textFormatted.isEmpty())
			return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView numberPlateText = (TextView) layout.findViewById(R.id.number_plate);
                numberPlateText.setText(textFormatted);

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 100);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(layout);
				toast.show();

				Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				mVibrator.vibrate(300);
			}
		});
	}

	private void displayLicensePlateToast(final String text) {
		final String textFormatted = text.replaceAll("\\s+","");

		if(textFormatted.isEmpty())
			return;

		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout_root));

				TextView numberPlateText = (TextView) layout.findViewById(R.id.number_plate);
				numberPlateText.setText(textFormatted);

				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.BOTTOM, 0, 100);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.show();

				Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				mVibrator.vibrate(300);
			}
		});
	}

	@SuppressLint("NewApi")
	private int getSoftbuttonsbarHeight() {
		// getRealMetrics is only available with API 17 and +
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int usableHeight = metrics.widthPixels;
			getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
			int realHeight = metrics.widthPixels;
			if (realHeight > usableHeight)
				return realHeight - usableHeight;
			else
				return 0;
		}
		return 0;
	}

	public void writeStatus(String statusText) {
		Calendar c = Calendar.getInstance();

		if(status != null)
			status.setText(c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND) + ": " + statusText);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VideoView videoView = (VideoView)findViewById(R.id.video_view);
		videoView.stopPlayback();
        isActive = false;
        try {
            socketConnection.stop();
        } catch (Exception e) {}
	}

	protected void joyStickInit() {
		final RelativeLayout joystickContainer = (RelativeLayout)findViewById(R.id.joystick_container);
		final RelativeLayout joystickAnalog = (RelativeLayout)findViewById(R.id.joystick_analog);

		joystick = new Joystick(ConnectionActivity.this, joystickContainer, joystickAnalog);
        joystick.bind();
        joystick.addListener(new MoveListener() {
            @Override
            public void onMove(int x, int y) {
                int value = Calculation.calculateValue(x, y);
                try {
                    System.out.println("Value: " + value);
                    socketConnection.write(1, new byte[]{(byte) value});
				} catch (Exception e) {

				}
			}
		});
	}

	private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	private void toggle() {
		if (mVisible) {
			hide();
		} else {
			show();
		}
	}

	private void hide() {
		/*ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}*/
		status.setVisibility(View.GONE);
		wallHitHolder.setVisibility(View.GONE);
		licensePlateButton.setVisibility(View.GONE);
		mVisible = false;

		// Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
		mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
	}

	private final Runnable mHidePart2Runnable = new Runnable() {
		@SuppressLint("InlinedApi")
		@Override
		public void run() {
			// Delayed removal of status and navigation bar

			// Note that some of these constants are new as of API 16 (Jelly Bean)
			// and API 19 (KitKat). It is safe to use them, as they are inlined
			// at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		}
	};

	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@SuppressLint("InlinedApi")
	private void show() {
		// Show the system bar
		mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

		mVisible = true;

		// Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
		mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
	}

	private final Runnable mShowPart2Runnable = new Runnable() {
		@Override
		public void run() {
			// Delayed display of UI elements
			/*ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.show();
			}*/


			licensePlateButton.setVisibility(View.VISIBLE);

			ViewGroup.MarginLayoutParams statusLayoutParams = (ViewGroup.MarginLayoutParams) status.getLayoutParams();
			//statusLayoutParams.setMargins(0, 0, getSoftbuttonsbarHeight() + 100, 0); // llp.setMargins(left, top, right, bottom);
			statusLayoutParams.rightMargin = getSoftbuttonsbarHeight();

			ViewGroup.MarginLayoutParams wallHitHolderParams = (ViewGroup.MarginLayoutParams) wallHitHolder.getLayoutParams();
			//wallHitHolderParams.setMargins(0, 0, getSoftbuttonsbarHeight() + 100, 0); // llp.setMargins(left, top, right, bottom);
			wallHitHolderParams.rightMargin = getSoftbuttonsbarHeight();

			status.setVisibility(View.VISIBLE);
			status.setLayoutParams(statusLayoutParams);
			wallHitHolder.setVisibility(View.VISIBLE);
			wallHitHolder.setLayoutParams(wallHitHolderParams);
		}
	};


	private void compassModule(final byte[] degreesData) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {

				float currentDegree = 0f;
				ImageView compass = (ImageView) findViewById(R.id.compass);

				float degree = 0;
				try {
					int i;
					for (i = 0; i < degreesData.length && degreesData[i] != 0; i++);

					String degreeString = new String(degreesData, 0, i, "us-ascii");
					degree = Math.round(Integer.parseInt(degreeString));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				RotateAnimation turn = new RotateAnimation(lastCompassRotation, degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

				// how long the animation will take place
				turn.setDuration(210);

				// set the animation after the end of the reservation status
				turn.setFillAfter(true);

				// Start the animation
				compass.startAnimation(turn);

				lastCompassRotation = degree;
			}
		});
	}

	private void speedModule(final byte[] speed) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				TextView speedText = (TextView)findViewById(R.id.speed_field);
				try {
					speedText.setText(new String(speed, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void distanceModule(final byte[] distance) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				TextView distanceText = (TextView)findViewById(R.id.distance_field);
				try {
					distanceText.setText(new String(distance, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void wallStopModule(final byte[] value) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				CheckBox wallHitCheckbox = (CheckBox)findViewById(R.id.wall_hit);

				if(wallHitCheckbox.isChecked() && value[0] == 0) {
					wallHitCheckbox.setChecked(false);
				} else if(!wallHitCheckbox.isChecked() && value[0] == 1) {
					wallHitCheckbox.setChecked(true);
				}

			}
		});
	}

	class ClientThread implements Runnable {

		@Override
		public void run() {
			socketConnection = new SocketClient(connection.ip, Integer.parseInt(connection.port));
			socketConnection.addListener(new DataReceiveListener() {
                @Override
                public void onDataReceive(int module, byte[] data) {
                    try {
                        writeStatus("Connected");
                        //System.out.println("Module: " + module);
                        //System.out.println("Data: " + new String(data, "UTF-8"));
                        switch (module) {
                            case 6:
                                compassModule(data);
                                break;
                            case 3:
                                speedModule(data);
                                break;
                            case 2:
                                distanceModule(data);
                                break;
                            case 7:
                                try {
                                    displayLicensePlateToast(data);
                                } catch (Exception e) {
                                    displayLicensePlateToast("ERROR!");
                                }
                                break;
							case 8:
								wallStopModule(data);
								break;
							case 9:
								Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
								mVibrator.vibrate((int)data[0] * 100);
								break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionDrop() {
                    if(isActive) {
                        if (!connection.reconnect) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ConnectionActivity.this, "Couldn't connect to " + connection.name + ".", Toast.LENGTH_SHORT).show();
                                }
                            });

                            ConnectionActivity.this.finish();
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    writeStatus("Connection dropped");
                                }
                            });

                            try {
                                Thread.sleep(4000);
                                socketConnection.reconnect();
                            } catch (Exception e) {

                            }
                        }
                    }
                }
            });
			socketConnection.setUp();
		}
	}
}
