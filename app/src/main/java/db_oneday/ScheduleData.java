package db_oneday;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ScheduleData{
	private final String tag = "ScheduleData";
	private OneDaydb onedaydb ;
	private String dbName;
	private Context mContext;
	public ScheduleData(Context context ,String string){
		mContext = context;
		dbName = string ;
		onedaydb = new OneDaydb(mContext,dbName);
	}
	
	//get total num 
	public int updateTotal(){
		int totalNum = 0;
		SQLiteDatabase db = onedaydb.getReadableDatabase();
		/*
		Cursor cursor = db.query(OneDaydb.TABLE_NAME, null, null, null, null, null, null);
		totalNum = cursor.getCount();//  ColumnCount();
		db.close();
		System.out.println("totalNum --->" + totalNum);
		*/
		Cursor cursor = db.rawQuery("select last_insert_rowid() from "+ OneDaydb.TABLE_NAME,null);  
		Log.i(tag, " Cursor = " + cursor);
		if(cursor.moveToFirst())  
			totalNum = cursor.getInt(0);
		Log.i(tag, " totalNum = " + totalNum);
		return totalNum;		
	}
	
    public void InsertData (String name ,String from,String to,String week){
			
    		ContentValues values = new ContentValues();
			values.put(OneDaydb.COLUMN_FROM_TIME, from);
			values.put(onedaydb.COLUMN_TO_TIME, to);
			values.put(OneDaydb.COLUMN_WEEK, week);
			if(name != null)
				values.put(OneDaydb.COLUMN_PLAN,name);
			else
				values.put(OneDaydb.COLUMN_PLAN,"NONE");
			SQLiteDatabase db = onedaydb.getWritableDatabase();
			
			try {
				db.insert(OneDaydb.TABLE_NAME, null, values);
			}catch (Exception e) {
//				System.out.println("Error inserting--->" + dataAndTime+" --" +dateString+" --" + name);
	        }
//			System.out.println("insert --->" + dataAndTime+" --" +dateString+" --" + name);
			db.close();
    }
    
    public int InsertData (String title ,String from,String to){
		

		ContentValues values = new ContentValues();
//
//		values.put(OneDaydb.KEY_INT_ID, null);
		values.put(OneDaydb.COLUMN_PLAN, title);
		values.put(OneDaydb.COLUMN_FROM_TIME, from);
		values.put(OneDaydb.COLUMN_TO_TIME, to);
////		values.put(OneDaydb.COLUMN_WEEK, week);
//		values.put(OneDaydb.KEY_STR_PATH_AUDIO, audioPath);
//		values.put(OneDaydb.KEY_INT_YEAR, year);
//		values.put(OneDaydb.KEY_INT_MONTH, month);
//		values.put(OneDaydb.KEY_INT_DATE, date);
//		values.put(OneDaydb.KEY_INT_TIME, time);
//		values.put(OneDaydb.KEY_INT_CYCLE, cycle);
//		values.put(OneDaydb.KEY_INT_WEEK_OF_MONTH, week);
		
		
		SQLiteDatabase db = onedaydb.getWritableDatabase();
		long re = 0;
		try {			
			re = db.insert(OneDaydb.TABLE_NAME, null, values);
		}catch (Exception e) {			
			System.out.println("--InsertData--Error---"+title);
        }
		db.close();
		if(re == -1)
			return -1;
		Log.i(tag,"--InsertData--" + title);
		Log.i(tag, " Insert row = " + re );
		return (int)re;
    }
    
    public int DeleteData (int id){
    	
    	SQLiteDatabase db = onedaydb.getWritableDatabase();
    	int  re = 0;
		try {
			
		     db.delete(OneDaydb.TABLE_NAME, OneDaydb.COLUMN_ID+"=?", 
						   new String[]{String.valueOf(id)} );
		}catch (Exception e) {
			
			System.out.println("--DeleteData--Error---"+OneDaydb.COLUMN_ID + " = " + id);
        }
		Log.i(tag, " DeletaData row = " + re +"id = " + id);
		db.endTransaction();
		db.close();
		return re;
    }

    public int DeleteData (  String whereClause, String[] whereArgs){
    	
    	SQLiteDatabase db = onedaydb.getWritableDatabase();
    	int  re = 0;
		try {
			
			re = db.delete(OneDaydb.TABLE_NAME, whereClause, whereArgs);
		}catch (Exception e) {
			
			System.out.println("--InsertData--Error---"+whereClause + " = " + whereArgs);
        }
		return re;
    }
    
    // 通过id 获取需要的 值（字符串类型） 
    public String getStringItemDate(int id , String KEY)
    {
    	SQLiteDatabase db = onedaydb.getReadableDatabase();
    	Cursor cursor = db.query(OneDaydb.TABLE_NAME, 
    			new String[]{KEY}, 
    			OneDaydb.COLUMN_ID+"=?", 
    			new String[]{Integer.toString(id)}, 
    			null, null, null);
    	
    	if(cursor.moveToFirst() == false)
    		return null;
    	int cIndex = cursor.getColumnIndex(KEY);
    	String key_value = cursor.getString(cIndex);
    	cursor.close();
    	db.close();
    	return key_value;
    }
    
    // 通过id  获取需要的 值（int类型）
    public int getIntDate(int id , String KEY)
    {
    	SQLiteDatabase db = onedaydb.getReadableDatabase();
    	Cursor cursor = db.query(OneDaydb.TABLE_NAME, 
    			new String[]{KEY}, 
    			OneDaydb.COLUMN_ID+"=?", 
    			new String[]{Integer.toString(id)}, 
    			null, null, null);
    	if(cursor.moveToFirst() == false)
    		return -1;
    	int key_value = cursor.getInt(cursor.getColumnIndex(KEY));
    	cursor.close();
    	db.close();
    	return key_value;
    }
    
    // 查询 某一天的 事件
    public int[] getArrayQueryForDate(int year, int month, int date){
    	SQLiteDatabase db = onedaydb.getReadableDatabase();
    	Cursor cursor = db.query(OneDaydb.TABLE_NAME, 
    			new String[]{OneDaydb.COLUMN_ID}, 
    			OneDaydb.COLUMN_FROM_TIME +"=?"+" AND "+OneDaydb.COLUMN_TO_TIME +"=?"+" AND "+OneDaydb.COLUMN_WEEK +"=?", 
    			new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(date)}, null, null, null);
    	
    	int getTotal = cursor.getCount();
    	if(cursor.moveToFirst() == false)
    		return null;
    	int [] id_array = new int[getTotal] ;
    	int i = 0;
    	do{ 
			id_array[i] = cursor.getInt(cursor.getColumnIndex(OneDaydb.COLUMN_ID));
			i++;
    	}
    	while(cursor.moveToNext());
    	/*
		for(cursor.moveToFirst(); cursor.isAfterLast(); cursor.moveToNext()){
			int i = cursor.getColumnIndex(OneDaydb.KEY_INT_ID);
			id_array[i] = cursor.getInt(i);    			
		}*/
    	cursor.close();
    	db.close();
		return id_array;    	
    }
    
    //查询某月 某一周的事件
    public int[] getArrayQueryForDate(int year, int month, int date, int week ){
    	SQLiteDatabase db = onedaydb.getReadableDatabase();
    	Cursor cursor = db.query(OneDaydb.TABLE_NAME, 
    			new String[]{OneDaydb.COLUMN_ID}, 
    			OneDaydb.COLUMN_FROM_TIME +"=?"+" AND "+OneDaydb.COLUMN_TO_TIME +"=?"+" AND "+OneDaydb.COLUMN_WEEK +"=?", 
    			new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(week)}, null, null, null);
    	
    	int getTotal = cursor.getCount();
    	if(cursor.moveToFirst() == false)
    		return null;
    	int [] id_array = new int[getTotal] ;
    	int i = 0;
    	do{ 
			id_array[i] = cursor.getInt(cursor.getColumnIndex(OneDaydb.COLUMN_ID));
			i++;
    	}
    	while(cursor.moveToNext());
    	
    	cursor.close();
    	db.close();
		return id_array;    	
    }
    
    //查询某月的事件
    public int[] getArrayQueryForDate(int year, int month ){
    	SQLiteDatabase db = onedaydb.getReadableDatabase();
    	Cursor cursor = db.query(OneDaydb.TABLE_NAME, 
    			new String[]{OneDaydb.COLUMN_ID}, 
    			OneDaydb.COLUMN_FROM_TIME +"=?"+" AND "+OneDaydb.COLUMN_TO_TIME +"=?", 
    			new String[]{Integer.toString(year),Integer.toString(month)}, null, null, null);
    	
    	int getTotal = cursor.getCount();
    	if(cursor.moveToFirst() == false)
    		return null;
    	int [] id_array = new int[getTotal] ;
    	int i = 0;
    	do{ 
			id_array[i] = cursor.getInt(cursor.getColumnIndex(OneDaydb.COLUMN_ID));
			i++;
    	}
    	while(cursor.moveToNext());
    	cursor.close();
    	db.close();
		return id_array;    	
    }
    //查询所有事件
    public int[] getArrayQueryForDate(){
    	SQLiteDatabase db = onedaydb.getReadableDatabase();
    	Cursor cursor = db.query(OneDaydb.TABLE_NAME, 
    			new String[]{OneDaydb.COLUMN_ID}, 
    			null, 
    			null, null, null, null);
    	
    	int getTotal = cursor.getCount();
    	if(cursor.moveToFirst() == false)
    		return null;
    	int [] id_array = new int[getTotal] ;
    	int i = 0;
    	do{ 
			id_array[i] = cursor.getInt(cursor.getColumnIndex(OneDaydb.COLUMN_ID));
			i++;
    	}
    	while(cursor.moveToNext());
    	cursor.close();
    	db.close();
		return id_array;    	
    }
    
    public void  UpdateData(int id,String title ,long from,long to, int week){
			ContentValues values = new ContentValues();

			values.put(OneDaydb.COLUMN_ID, id);
			values.put(OneDaydb.COLUMN_PLAN, title);
			values.put(OneDaydb.COLUMN_FROM_TIME, from);
			values.put(OneDaydb.COLUMN_TO_TIME, to);
			values.put(OneDaydb.COLUMN_WEEK, week);
//			values.put(OneDaydb.KEY_STR_PATH_AUDIO, audioPath);
//			values.put(OneDaydb.KEY_INT_YEAR, year);
//			values.put(OneDaydb.KEY_INT_MONTH, month);
//			values.put(OneDaydb.KEY_INT_DATE, date);
//			values.put(OneDaydb.KEY_INT_TIME, time);
//			values.put(OneDaydb.KEY_INT_CYCLE, cycle);
//			values.put(OneDaydb.KEY_INT_WEEK_OF_MONTH, week);
			
			
			SQLiteDatabase db = onedaydb.getWritableDatabase();
			long re = -1;
			try {
				re = db.update(OneDaydb.TABLE_NAME, values, 
						OneDaydb.COLUMN_ID+"=?", new String[]{""+id});
			}catch (Exception e) {			
				System.out.println("--updateData--Error---"+title);
	        }
			db.close();
			Log.i(tag,"--update--" + title);
			Log.i(tag, " update row = " + re );

    }
    
    public void close(){
    	if(onedaydb != null)
    		onedaydb.close();    	
    }
    
    public String[] getDataSring(){
		return null;
    	
    }
}

