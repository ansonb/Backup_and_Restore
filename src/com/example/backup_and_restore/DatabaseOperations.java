package com.example.backup_and_restore;

import com.example.backup_and_restore.settingsTableInfo.settingsDBTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.SyncStateContract.Columns;
import android.util.Log;



public class DatabaseOperations extends SQLiteOpenHelper{

	public static final int databaseVersion=1;
	public String QUERRY="CREATE TABLE "+settingsDBTable.TABLE_NAME+"("
			+settingsDBTable.PATH_TYPE+" TEXT,"
			+settingsDBTable.PATH+" TEXT,"
			+settingsDBTable.SAVE_TO_DRIVE+" TEXT,"
			+settingsDBTable.CONTACTS_BACKUP_PATH+" TEXT,"
			+settingsDBTable.MESSAGES_BACKUP_PATH+" TEXT);";
	
	public DatabaseOperations(Context context) {
		super(context, settingsDBTable.DATABASE_NAME, null, databaseVersion);
		
		Cursor cr = getInfo(this);
		if(!cr.moveToFirst()){
			initialise(this);
		}
		Log.d("Database Operations ", "Database Created");
	}
	
	@Override
	public void onCreate(SQLiteDatabase sdb)
	{
		Log.d("Database OPerations ", "Creating Table");
		sdb.execSQL(QUERRY);
		Log.d("Database OPerations ", "Table Created");
	}
	
	private void putInfo(DatabaseOperations dob, String key, String value)
	{
		Log.d("Database OPerations ", "Putting info");
		SQLiteDatabase SQ=dob.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(key, value);
		SQ.insert(settingsDBTable.TABLE_NAME, null, cv);
		Log.d("Database Operator", "Row Inserted");
	}
	
	private void initialise(DatabaseOperations dob){
		SQLiteDatabase SQ=dob.getWritableDatabase();
				
		ContentValues cv = new ContentValues();
		cv.put(settingsDBTable.PATH, MainActivity.defaultPath);
		cv.put(settingsDBTable.PATH_TYPE, settingsDBTable.PATH_TYPE_DEFAULT);
		cv.put(settingsDBTable.SAVE_TO_DRIVE, settingsDBTable.DRIVE_UNCHECKED);
		cv.put(settingsDBTable.CONTACTS_BACKUP_PATH, "");
		cv.put(settingsDBTable.MESSAGES_BACKUP_PATH, "");
		
		SQ.insert(settingsDBTable.TABLE_NAME, null, cv);

	}
	
	public Cursor getInfo(DatabaseOperations dob)
	{
		Log.d("Database OPerations ", "Getting info");
		SQLiteDatabase SQ = dob.getReadableDatabase();
		Log.d("DbOps", "Got Readable db");
		
		String Columns[]={settingsDBTable.PATH_TYPE,
				settingsDBTable.PATH,
				settingsDBTable.SAVE_TO_DRIVE,
				settingsDBTable.CONTACTS_BACKUP_PATH,
				settingsDBTable.MESSAGES_BACKUP_PATH};
		Cursor CR = SQ.query(settingsDBTable.TABLE_NAME, Columns,
				null, null, null, null, null, null);
		Log.d("DbOps", "returning CR");
		return CR;
	}

	 public void delete(DatabaseOperations DOP, String text)
	 {
	  String selection = settingsDBTable.PATH+ " LIKE ?";
	  String args[] = {text};
	  SQLiteDatabase SQ = DOP.getWritableDatabase();
	  SQ.delete(settingsDBTable.TABLE_NAME, selection, args);
	  
	 }

	public void Update(DatabaseOperations DOP, String key, String value) 
	{
	    SQLiteDatabase SQ  = DOP.getWritableDatabase();
	    
	    ContentValues values = new ContentValues();
		values.put(key, value);

		SQ.update(settingsDBTable.TABLE_NAME, values, null, null);
		Log.d("Profile Database OPerations", "Row Updated");
			
		}	 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
