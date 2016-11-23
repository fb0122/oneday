package db_oneday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntDef;

import com.example.fb0122.oneday.utils.TimeCalendar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class OneDaydb extends SQLiteOpenHelper {

    private static final String TAG = "OneDayDb";

    public static final String TABLE_NAME = "oneday";
    public static final String COLUMN_FROM_TIME = "from_time";
    public static final String COLUMN_PLAN = "plan";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TO_TIME = "to_time";
    public static final String COLUMN_WEEK = "week";
    public static final String COLUMN_WEEK_IN_YEAR = "week_in_year";
    public static final String COLUMN_DONE = "done_plan";

    public String name;


    public static SQLiteDatabase dbr;

    public OneDaydb(Context context, String name) {
        this(context, name, 1);
    }

    public OneDaydb(Context context, String name, int VERSION) {
        super(context, "oneday", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PLAN+ " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_FROM_TIME + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_TO_TIME + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_WEEK + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_WEEK_IN_YEAR + " INTEGER NOT NULL DEFAULT \"\","
                + COLUMN_DONE + " INTEGER DEFAULT 0" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    /* 仅根据习惯名称更新习惯 ,如滑动修改 */
    public void updateData(String table, ContentValues cv, String plan) {
        dbr = getWritableDatabase();
        dbr.update(table, cv, COLUMN_PLAN + " =?", new String[]{plan});
    }

    /* 根据习惯名称与开始时间更新习惯,如展开修改 */
    public void updataData(String table, ContentValues cv, String timColumn,String time, String plan){
        if (dbr != null) {
            dbr = getWritableDatabase();
        }
        if (dbr != null) {
            dbr.update(table, cv, COLUMN_PLAN + " =? and " + timColumn + " = ?", new String[]{plan,time});
        }
    }

    public Cursor Query() {
        dbr = getReadableDatabase();
        Cursor c = dbr.rawQuery(" select * from " + OneDaydb.TABLE_NAME, null);
        return c;
    }

    /**
     * 根据星期查询今天的计划
     */

    public Cursor Query(String week) {
        dbr = getReadableDatabase();
        Cursor c = dbr.rawQuery(" select * from " + OneDaydb.TABLE_NAME + " where week = " + "'" + week + "'", null);
        return c;
    }


    public void delete(int id) {
        if (dbr != null) {
            dbr = getWritableDatabase();
            dbr.delete(OneDaydb.TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
            dbr.close();
        }
    }

    /**
     * 插入数据
     */
    public void insertDta(String plan,String from,String to,String week){
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(COLUMN_PLAN, plan);
            contentValues.put(COLUMN_FROM_TIME,from);
            contentValues.put(COLUMN_TO_TIME,to);
            contentValues.put(COLUMN_WEEK, week);
            contentValues.put(COLUMN_WEEK_IN_YEAR,TimeCalendar.getWeekInYear());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        dbr = getWritableDatabase();
        dbr.insert(TABLE_NAME,null,contentValues);
        dbr.close();
    }

    /**
     * 得到时间并添加提醒
     * @param fromTime
     * @return
     */
    public String[] getNotifyInfo(String fromTime) {
        dbr = getReadableDatabase();
        String time = "";
        String custom = "";
        Cursor c = dbr.rawQuery(" select * from " + OneDaydb.TABLE_NAME + " where " + OneDaydb.COLUMN_FROM_TIME + " = "
                + "'" + fromTime + "'", null);
        if (c.moveToFirst()) {
            time = c.getString(c.getColumnIndex(OneDaydb.COLUMN_TO_TIME));
            custom = c.getString(c.getColumnIndex(OneDaydb.COLUMN_PLAN));
        }
        c.close();
        return new String[]{time, custom};
    }


    @IntDef({0,1})
    @Retention(RetentionPolicy.SOURCE)
    public @interface isFinish{

    }

    /*
    *   完成计划时将相应的数据库内 COLUMN_DONE 列的值改为 1。value 的值只能为 0 或 1
    */

    public void planDone(String plan,int weekInYear,@isFinish int value){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DONE,value);
        dbr.update(TABLE_NAME,cv,COLUMN_PLAN  + " = ? and " + COLUMN_WEEK_IN_YEAR + " = ? ",
                new String[]{plan,String.valueOf(weekInYear)});
    }

    /*
    *   获取每天计划完成率
    */
    public int finishPercent(String week){
        if (dbr == null) {
            dbr = getReadableDatabase();
        }
        Cursor c = dbr.rawQuery(" select " + COLUMN_DONE + " from " + TABLE_NAME + " where "
                + COLUMN_WEEK_IN_YEAR + " = " + TimeCalendar.getWeekInYear() + " and "
                + COLUMN_WEEK + " = '" + week + "'",null);
        if (c == null){
            return 0;
        }
        float totalFinish = 0;
        if (c.moveToFirst()) {
            do{
                if (c.getInt(0) == 1){
                    totalFinish += 1;
                }
            }while (c.moveToNext());
        }
        c.close();
        int percent = (int)(totalFinish/c.getCount() * 100);
        return percent;
    }


}
