package hva.row8.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hva.row8.MainActivity;
import hva.row8.R;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public class ConnectionAddDialog extends Dialog {
	private MainActivity activity;
	private Button accept;
	private Button cancel;

	public ConnectionAddDialog(MainActivity activity) {
		super(activity);

		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.connection_add_dialog);

		accept = (Button)findViewById(R.id.connection_add_button);
		cancel = (Button)findViewById(R.id.connection_add_cancel);

		accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText ip = (EditText)findViewById(R.id.connection_add_ip);
				EditText port = (EditText)findViewById(R.id.connection_add_port);
				EditText name = (EditText)findViewById(R.id.connection_add_name);

				if(ip.getText().length() == 0) {
					Toast.makeText(activity, "Please fill in an IP.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(port.getText().length() == 0) {
					Toast.makeText(activity, "Please fill in a portnumber.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(name.getText().length() == 0) {
					Toast.makeText(activity, "Please fill in a name of the connection.", Toast.LENGTH_SHORT).show();
					return;
				}

				activity.application.connectionDataSource.addConnection(String.valueOf(ip.getText()), String.valueOf(port.getText()), String.valueOf(name.getText()));
				Toast.makeText(activity, "Succesfully added.", Toast.LENGTH_SHORT).show();
				activity.renewConnectionList();
				dismiss();
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
