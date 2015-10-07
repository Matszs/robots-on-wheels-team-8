package hva.row8;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Toast;

import hva.row8.Classes.Connection;
import hva.row8.Interfaces.DataReceiveListener;
import hva.row8.Modules.SocketClient;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ConnectionActivity extends AppCompatActivity {
	private View mContentView;
	public Application application;
	private Connection connection;
	SocketClient socketConnection;

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
		mContentView = findViewById(R.id.fullscreen_content);

		makeDisplayFullscreen();

		//new Thread(new ClientThread()).start();

		Button socketTestButton = (Button)findViewById(R.id.test_socket);
		socketTestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					socketConnection.write(1, new byte[]{ 0 });
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});

		compassModule();
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

	private void compassModule() {
		float currentDegree = 0f;
		ImageView compass = (ImageView)findViewById(R.id.compass);

		float degree = Math.round(180);
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
		compass.startAnimation(turn);
		currentDegree = -degree;
	}

	class ClientThread implements Runnable {

		@Override
		public void run() {
			socketConnection = new SocketClient(connection.ip, Integer.parseInt(connection.port));
			socketConnection.setUp();
			socketConnection.addListener(new DataReceiveListener() {
				@Override
				public void onDataReceive(int module, byte[] data) {
					try {
						System.out.println("Module: " + module);
						System.out.println("Data: " + new String(data, "UTF-8"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
