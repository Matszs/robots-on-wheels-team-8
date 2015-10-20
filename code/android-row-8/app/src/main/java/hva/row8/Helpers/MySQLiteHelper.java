package hva.row8.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mats on 27-4-2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hva_row8_.db";
    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_CREATE = "create table connections (id integer primary key, name TEXT, ip TEXT, port TEXT);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if(oldVersion < newVersion)
            database.execSQL("DROP TABLE IF EXISTS connections");
        onCreate(database);
    }
}
