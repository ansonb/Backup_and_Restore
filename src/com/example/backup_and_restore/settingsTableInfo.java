package com.example.backup_and_restore;

import android.os.Environment;
import android.provider.BaseColumns;

public class settingsTableInfo{
	
	public settingsTableInfo(){
		
	}
	public static abstract class settingsDBTable implements BaseColumns{
		public static final String PATH = "path";
		public static final String PATH_TYPE = "pathType";
		public static final String SAVE_TO_DRIVE = "saveToDrive";
		public static final String MESSAGES_BACKUP_PATH = "messagesBackupPath";
		public static final String CONTACTS_BACKUP_PATH = "contactsBavkupPath";
		public static final String TABLE_NAME = "settingsTable";		
		public static final String DATABASE_NAME = "Settings";		
		
		public static final int PATH_COLUMN = 1;
		public static final int PATH_TYPE_COLUMN = 0;
		public static final int SAVE_TO_DRIVE_COLUMN = 2;
		public static final int CONTACTS_BACKUP_PATH_COLUMN = 3;
		public static final int MESSAGES_BACKUP_COLUMN = 4;
		
		public static final String PATH_TYPE_DEFAULT = "Default";
		public static final String PATH_TYPE_CUSTOM = "Custom";
		public static final String DRIVE_CHECKED = "Checked";
		public static final String DRIVE_UNCHECKED = "Unchecked";

	}

	
}
