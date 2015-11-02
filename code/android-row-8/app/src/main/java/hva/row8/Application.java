package hva.row8;

import android.util.Log;

import hva.row8.DataSources.ConnectionDataSource;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public class Application extends android.app.Application {
	public ConnectionDataSource connectionDataSource;

	@Override
	public void onCreate() {
		super.onCreate();

		connectionDataSource = new ConnectionDataSource(this);
		try {
			connectionDataSource.open();
		} catch (Exception e) {
			Log.w(Application.class.getName(), " Error creating connection to the ConnectionDataSource.");
		}
	}
}
