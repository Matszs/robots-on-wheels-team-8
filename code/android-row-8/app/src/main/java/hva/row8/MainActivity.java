package hva.row8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import hva.row8.Dialogs.ConnectionAddDialog;

public class MainActivity extends AppCompatActivity {
	public Application application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		application = (Application)getApplication();
		renewConnectionList();
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





	}
}
