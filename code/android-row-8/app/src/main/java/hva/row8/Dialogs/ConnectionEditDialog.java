package hva.row8.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hva.row8.Classes.Connection;
import hva.row8.MainActivity;
import hva.row8.R;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public class ConnectionEditDialog extends Dialog {
	private MainActivity activity;
	private Button save;
	private Button cancel;
	private Button remove;

	private EditText ip;
	private EditText port;
	private EditText name;

	private Connection connection;

	public ConnectionEditDialog(MainActivity activity) {
		super(activity);

		this.activity = activity;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.connection_edit_dialog);

		if(connection == null) {
			Toast.makeText(activity, "Connection to edit not found.", Toast.LENGTH_SHORT).show();
			dismiss();
		}

		save = (Button)findViewById(R.id.connection_edit_save_button);
		cancel = (Button)findViewById(R.id.connection_edit_cancel);
		remove = (Button)findViewById(R.id.connection_edit_delete);

		ip = (EditText)findViewById(R.id.connection_edit_ip);
		port = (EditText)findViewById(R.id.connection_edit_port);
		name = (EditText)findViewById(R.id.connection_edit_name);

		ip.setText(connection.ip);
		port.setText(connection.port);
		name.setText(connection.name);

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ip.getText().length() == 0) {
					Toast.makeText(activity, "Please fill in an IP.", Toast.LENGTH_SHORT).show();
					return;
				}
				if (port.getText().length() == 0) {
					Toast.makeText(activity, "Please fill in a portnumber.", Toast.LENGTH_SHORT).show();
					return;
				}
				if (name.getText().length() == 0) {
					Toast.makeText(activity, "Please fill in a name of the connection.", Toast.LENGTH_SHORT).show();
					return;
				}

				activity.application.connectionDataSource.editConnection(connection.id, String.valueOf(ip.getText()), String.valueOf(port.getText()), String.valueOf(name.getText()));
				Toast.makeText(activity, "Succesfully editted.", Toast.LENGTH_SHORT).show();
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

		remove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(activity, "Succesfully deleted.", Toast.LENGTH_SHORT).show();
				dismiss();
			}
		});
	}
}
