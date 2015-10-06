package hva.row8.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import hva.row8.Classes.Connection;
import hva.row8.R;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public class ConnectionAdapter extends ArrayAdapter<Connection> {

	public ConnectionAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public ConnectionAdapter(Context context, int resource, List<Connection> items) {
		super(context, resource, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View itemHolder = convertView;

		if (itemHolder == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			itemHolder = vi.inflate(R.layout.connection_single_view, null);
		}

		Connection connection = getItem(position);

		TextView name = (TextView)itemHolder.findViewById(R.id.connection_name);
		name.setText(connection.name);

		TextView address = (TextView)itemHolder.findViewById(R.id.connection_address);
		address.setText(connection.ip + ":" + connection.port);


		return itemHolder;
	}

	@Override
	public long getItemId(int position) {
		Connection connection = getItem(position);
		return connection.id;
	}

}
