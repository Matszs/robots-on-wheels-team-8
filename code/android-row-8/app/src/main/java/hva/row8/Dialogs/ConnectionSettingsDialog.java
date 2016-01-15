package hva.row8.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import hva.row8.ConnectionActivity;
import hva.row8.MainActivity;
import hva.row8.R;

/**
 * Created by matsotten on 06/01/16.
 */
public class ConnectionSettingsDialog extends Dialog {
    private ConnectionActivity activity;

    public ConnectionSettingsDialog(ConnectionActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connection_settings_dialog);

        CheckBox wallHit = (CheckBox)findViewById(R.id.wall_hit);

        wallHit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    activity.socketConnection.write(8, new byte[]{(byte) (isChecked ? 1 : 0)});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton licensePlate = (ImageButton)findViewById(R.id.read_license_plate);
        licensePlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.socketConnection.write(7, new byte[]{});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });

        Button closeButton = (Button)findViewById(R.id.close_settings_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
