package db_oneday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class OneDaydb extends SQLiteOpenHelper {

    public static final String TAG = "OneDayDb";

    public static final String TABLE_NAME = "oneday";
    public static final String COLUMN_FROM_TIME = "from_time";
    public static final String COLUMN_PLAN = "plan";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TO_TIME = "to_time";
    public static final String COLUMN_WEEK = "week";
    public String name;


    public SQLiteDatabase dbr;

    public OneDaydb(Context context, String name) {
        this(context, name, 1);
    }

    public OneDaydb(Context context, String name, int VERSION) {
        super(context, "oneday", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PLAN
                + " TEXT NOT NULL DEFAULT \"\"," + COLUMN_FROM_TIME
                + " TEXT NOT NULL DEFAULT \"\"," + COLUMN_TO_TIME + " TEXT NOT NULL DEFAULT \"\"," + COLUMN_WEEK + " TEXT NOT NULL DEFAULT \"\"" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void updateData(String table,ContentValues cv,String time_value,String plan){
        dbr = getWritableDatabase();
        dbr.update(table,cv,OneDaydb.COLUMN_PLAN + " =?",new String[]{plan});
    }

    public Cursor Query() {
        dbr = getReadableDatabase();
        Cursor c = dbr.rawQuery(" select * from " + OneDaydb.TABLE_NAME, null);
        return c;
    }

    /**根据星期查询今天的计划*/

    public Cursor Query(String week){
        dbr = getReadableDatabase();
        Cursor c = dbr.rawQuery(" select * from " + OneDaydb.TABLE_NAME + " where week = " + "'" + week + "'",null);
        return c;
    }

    public void delete(int id) {
        if (dbr != null) {
            dbr = getWritableDatabase();
            dbr.delete(OneDaydb.TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
            dbr.close();
        }
    }


}
