package hva.row8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hva.row8.Adapters.ConnectionAdapter;
import hva.row8.Classes.Connection;
import hva.row8.Dialogs.ConnectionAddDialog;
import hva.row8.Dialogs.ConnectionEditDialog;

public class MainActivity extends AppCompatActivity {
	public Application application;
	private ArrayAdapter adapter = null;
	private ArrayList<Connection> connections;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		application = (Application)getApplication();

		ListView connectionsList = (ListView)findViewById(R.id.connection_list);
		connections = application.connectionDataSource.getConnections();
		adapter = new ConnectionAdapter(MainActivity.this, R.layout.connection_single_view, connections);
		connectionsList.setAdapter(adapter);
		connectionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Connection connection = application.connectionDataSource.getConnection((int)id);

				Intent connectionIntent = new Intent(MainActivity.this, ConnectionActivity.class);
				connectionIntent.putExtra("connection_id", (int)id);
				startActivity(connectionIntent);

				Toast.makeText(MainActivity.this, "Connecting to " + connection.name, Toast.LENGTH_SHORT).show();
			}
		});
		connectionsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				ConnectionEditDialog connectionEditDialog = new ConnectionEditDialog(MainActivity.this);
				connectionEditDialog.setConnection(application.connectionDataSource.getConnection((int)id));
				connectionEditDialog.show();

				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add:

				ConnectionAddDialog connectionAddDialog = new ConnectionAddDialog(this);
				connectionAddDialog.show();

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void renewConnectionList() {
		ListView connectionsList = (ListView)findViewById(R.id.connection_list);
		connections = application.connectionDataSource.getConnections();
		adapter.clear();
		adapter.addAll(connections);
		adapter.notifyDataSetChanged();
	}
}
