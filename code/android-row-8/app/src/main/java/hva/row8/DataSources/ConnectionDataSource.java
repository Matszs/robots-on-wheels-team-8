package hva.row8.DataSources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hva.row8.Classes.Connection;
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

	public void deleteConnection(int id) {
		database.delete("connections", "id=?", new String[]{String.valueOf(id)});
	}

	public Connection getConnection(int id) {
		Cursor connectionCursor = database.query("connections", new String[]{"id", "name", "ip", "port"}, "id = ?", new String[]{ String.valueOf(id) }, null, null, null);
		if(connectionCursor.moveToFirst())
			return new Connection(connectionCursor.getInt(0), connectionCursor.getString(1), connectionCursor.getString(2), connectionCursor.getString(3));
		return null;
	}

	public ArrayList<Connection> getConnections() {
		ArrayList<Connection> connections = new ArrayList<Connection>();

		Cursor cursor = database.rawQuery("SELECT * FROM connections ORDER BY id DESC", null);

		if (cursor.moveToFirst()) {
			do {
				connections.add(new Connection(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
			} while (cursor.moveToNext());
		}

		return connections;

	}
}
