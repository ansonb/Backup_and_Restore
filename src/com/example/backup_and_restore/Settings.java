package com.example.backup_and_restore;

import com.example.backup_and_restore.settingsTableInfo.settingsDBTable;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends ActionBarActivity{
	
	EditText customPath;
	RadioGroup radioGroup;
	CheckBox driveCheckBox;
	
	DatabaseOperations DOP;
	
	String path;
	String pathType;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		customPath = (EditText) findViewById(R.id.customPath);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		driveCheckBox = (CheckBox) findViewById(R.id.checkBox1);
		
		DOP = new DatabaseOperations(getApplicationContext());
		setLayout();

		customPath.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				DOP.Update(DOP, settingsDBTable.PATH, customPath.getText().toString());
			}
			
		});
		
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); 
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.defaultPathOp:
	            if (checked){
	            	customPath.setVisibility(View.INVISIBLE);
	            	DOP.Update(DOP, settingsDBTable.PATH_TYPE
	            			, settingsDBTable.PATH_TYPE_DEFAULT);
	            }
	            break;
	        case R.id.customPathOp:
	            if (checked){
	            	customPath.setVisibility(View.VISIBLE);
	            	DOP.Update(DOP, settingsDBTable.PATH_TYPE
	            			, settingsDBTable.PATH_TYPE_CUSTOM);
	            	
	            	Cursor cr = DOP.getInfo(DOP);
	            	if(cr.moveToFirst()){
	            		customPath.setText(cr.getString(settingsDBTable.PATH_COLUMN));
	            	}
	            	
	            }
	            break;
	    }
	}	
	
	public void setLayout(){
		Cursor cr = DOP.getInfo(DOP);
		if(cr.moveToFirst()){
			if(cr.getString(settingsDBTable.SAVE_TO_DRIVE_COLUMN)
					.equals(settingsDBTable.DRIVE_CHECKED) ){
				driveCheckBox.setChecked(true);
			}else{
				driveCheckBox.setChecked(false);
			}
			path = cr.getString(settingsDBTable.PATH_COLUMN);
			pathType = cr.getString(settingsDBTable.PATH_TYPE_COLUMN);
			
			if(pathType.equals(settingsDBTable.PATH_TYPE_DEFAULT)){
				radioGroup.check(R.id.defaultPathOp);
				customPath.setVisibility(View.INVISIBLE);
			}else{
				radioGroup.check(R.id.customPathOp);
				customPath.setVisibility(View.VISIBLE);
				customPath.setText(cr.getString(settingsDBTable.PATH_COLUMN));
			}
		}		
	}
	
	public void onCheckBoxClicked(View v){
		boolean checked = ((CheckBox) v).isChecked();
		
		if(checked){
			DOP.Update(DOP, settingsDBTable.SAVE_TO_DRIVE, settingsDBTable.DRIVE_CHECKED);
		}else{
			DOP.Update(DOP, settingsDBTable.SAVE_TO_DRIVE, settingsDBTable.DRIVE_UNCHECKED);			
		}
		
	}
}
