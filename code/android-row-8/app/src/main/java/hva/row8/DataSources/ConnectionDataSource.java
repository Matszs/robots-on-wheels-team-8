package hva.row8.DataSources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import hva.row8.Helpers.MySQLiteHelper;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public class ConnectionDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public ConnectionDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void addConnection(String ip, String port, String name) {
		ContentValues values = new ContentValues();

		int id = 1;
		Cursor existingConnection = database.query("connections", new String[] { "id" }, "", new String[] {}, "", "", "id DESC");
		if(existingConnection.moveToFirst())
			id = (existingConnection.getInt(0)) + 1;

		values.put("id", id);
		values.put("ip", ip);
		values.put("port", port);
		values.put("name", name);

		database.insert("connections", null, values);
	}

	public void editConnection(int id, String ip, String port, String name) {
		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("ip", ip);
		values.put("port", port);
		values.put("name", name);

		database.update("connections", values, "id=?", new String[]{String.valueOf(id)});
	}
}
