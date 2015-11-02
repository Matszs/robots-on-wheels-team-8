package hva.row8;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.UnsupportedEncodingException;

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
	private View mContentView;
	public Application application;
	private Connection connection;
	private float lastCompassRotation = 0;
	SocketClient socketConnection;
	MediaController mediaController;

	private int xHolder;
	private int yHolder;

	private Joystick joystick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);

		application = (Application)getApplication();

		Intent connectionIntent = getIntent();
		int connectionId = connectionIntent.getIntExtra("connection_id", 0);

		if(connectionId == 0) {
			Toast.makeText(ConnectionActivity.this, "Connection cannot be made. [connection_id not found]", Toast.LENGTH_SHORT).show();
			this.finish();
		}

		connection = application.connectionDataSource.getConnection(connectionId);
		mContentView = findViewById(R.id.video_view);

		VideoView videoView = (VideoView)findViewById(R.id.video_view);

		//Uri UriSrc = Uri.parse("http://" + connection.ip + ":8090");
		Uri UriSrc = Uri.parse("rtsp://" + connection.ip + ":8554/unicast");

		videoView.setVideoURI(UriSrc);
		//mediaController = new MediaController(ConnectionActivity.this);
		//videoView.setMediaController(mediaController);
		videoView.start();

		makeDisplayFullscreen();
		joyStickInit();

		new Thread(new ClientThread()).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VideoView videoView = (VideoView)findViewById(R.id.video_view);
		videoView.stopPlayback();
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
					socketConnection.write(1, new byte[]{(byte) value});
					System.out.println("Value: " + value);
				} catch (Exception e) {

				}

				//System.out.println("X: " + x + " | Y: " + y);
				//System.out.println("V: " + value);

			}
		});
	}

	@Override
	protected void onStop() {
		try {
			socketConnection.stop();
		} catch (Exception e) {
			// If there is no socket connection or anything, not a problem.
		}
		super.onStop();
	}

	private void makeDisplayFullscreen() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.hide();
		}

		mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

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

	class ClientThread implements Runnable {

		@Override
		public void run() {
			socketConnection = new SocketClient(connection.ip, Integer.parseInt(connection.port));
			socketConnection.addListener(new DataReceiveListener() {
				@Override
				public void onDataReceive(int module, byte[] data) {
					try {
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

						}


					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onConnectionDrop() {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(ConnectionActivity.this, "Couldn't connect to server.", Toast.LENGTH_SHORT).show();
						}
					});

					ConnectionActivity.this.finish();
				}
			});

					socketConnection.setUp();
				}
			}
		}
