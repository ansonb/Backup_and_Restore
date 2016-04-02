package com.example.backup_and_restore;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import com.example.backup_and_restore.settingsTableInfo.settingsDBTable;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FileExplorer extends Activity{

	ListView fileList;
	ArrayAdapter<File> mAdapter;
	ArrayAdapter<String> sAdapter;
	FileExplorerAdapter customAdapter;
	ArrayList<FileNameModel> modelList;
	TextView parentName;
	
	File sd;
    File[] list;
    String[] listOfFiles;
    
    String whoCalled;
        
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_list);
				
		Bundle bn;
		bn = getIntent().getExtras();
		whoCalled = bn.getString("whoCalled");
		
		fileList = (ListView) findViewById(R.id.listView1);
		parentName = (TextView) findViewById(R.id.parent_folder);
		
		sd = Environment.getExternalStorageDirectory();
		list = sd.listFiles();
		listOfFiles = sd.list();
			
		modelList = new ArrayList<FileNameModel> ();
		for(int i=0; i<list.length; i++){
			FileNameModel Model = new FileNameModel(list[i], listOfFiles[i]);
			modelList.add(Model);
		}
	    customAdapter = new FileExplorerAdapter(FileExplorer.this
	    		, modelList);
	    
	    mAdapter = new ArrayAdapter<File>(getApplicationContext()
	    		, R.layout.simple_list_item, list);
	    sAdapter = new ArrayAdapter<String> (getApplicationContext()
	    		, R.layout.simple_list_item, listOfFiles);
	    

	    fileList.setAdapter(customAdapter);
	    parentName.setText(sd.toString());
	    
	    fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				sd = mAdapter.getItem(position);
				if(sd.isDirectory()){
					list = sd.listFiles();
					listOfFiles = sd.list();
				    mAdapter = new ArrayAdapter<File>(getApplicationContext()
				    		, R.layout.simple_list_item, list);
				    sAdapter = new ArrayAdapter<String> (getApplicationContext()
				    		, R.layout.simple_list_item, listOfFiles);
				    
					modelList = new ArrayList<FileNameModel> ();
					for(int i=0; i<list.length; i++){
						FileNameModel Model = new FileNameModel(list[i], listOfFiles[i]);
						modelList.add(Model);
					}
				    customAdapter = new FileExplorerAdapter(FileExplorer.this
				    		, modelList);
				    
				    fileList.setAdapter(customAdapter);	
				    parentName.setText(sd.toString());
				    
				}else{
					Intent intent = new Intent(getApplicationContext(),MainActivity.class);
					intent.putExtra("whoCalled", whoCalled);
					intent.setData(Uri.fromFile(sd));
					startActivity(intent);
					
					MainActivity.ActivityLifeHandler.sendEmptyMessage(1);
					finish();
					
				}

			}
		});
		
	}
	
	@Override
	public void onBackPressed(){
		if(sd.getParent()==null){
			finish();
		}else{
			sd = new File(sd.getParent());
			list = sd.listFiles();
			listOfFiles = sd.list();
		    mAdapter = new ArrayAdapter<File>(getApplicationContext()
		    		, R.layout.simple_list_item, list);
		    sAdapter = new ArrayAdapter<String> (getApplicationContext()
		    		, R.layout.simple_list_item, listOfFiles);
		    
			modelList = new ArrayList<FileNameModel> ();
			for(int i=0; i<list.length; i++){
				FileNameModel Model = new FileNameModel(list[i], listOfFiles[i]);
				modelList.add(Model);
			}
		    customAdapter = new FileExplorerAdapter(FileExplorer.this
		    		, modelList);
		    
		    fileList.setAdapter(customAdapter);		
		    parentName.setText(sd.toString());
		}
	}

}
