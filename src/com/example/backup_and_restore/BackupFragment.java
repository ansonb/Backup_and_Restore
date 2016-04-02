package com.example.backup_and_restore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.example.backup_and_restore.settingsTableInfo.settingsDBTable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BackupFragment extends Fragment{
	
	String smsFile;
	
	String vfile;
	ArrayList<String> vCard;
	Cursor cursor;
	
	FileOutputStream mFileOutputStream;
	
	int width, height;
	
	DatabaseOperations DOP;
	
	static File sdContacts;
	static File sdMessages;
	
	public static View rootView;
	public TextView contacts;
	public TextView messages;
	public TextView drive;
	
	Activity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		rootView = inflater.inflate(R.layout.backup_2, container, false);
		
		contacts = (TextView) rootView.findViewById(R.id.contact_backup_image);
		messages = (TextView) rootView.findViewById(R.id.messages_backup_image);
		drive = (TextView) rootView.findViewById(R.id.drive_backup_image);
		
		DOP = new DatabaseOperations(getActivity());
		
		mActivity = getActivity();
		
		return rootView;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
		case 3:
			if (data==null) return;
	        saveToDriveFromPath(data.getData().getPath().toString());  			
			break;
		default:
			break;
		}
	}
	
	
	public void backupContacts(){
		MainActivity.mProgressDialog.setMessage("Backing up contacts");
		MainActivity.mProgressDialog.setTitle("Please Wait...");
		MainActivity.mProgressDialog.setCancelable(false);
		MainActivity.mProgressDialog.show();
		
		Thread mThread = new Thread(){
			@Override
			public void run(){
				Date d = new Date(System.currentTimeMillis());
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		        vfile = "Contacts_"+sdf.format(d).toString()+".vcf";
		        
		        if(getVcardString()){
		        	MainActivity.mProgressDialog.dismiss();
		      	
			        String msg = "Contacts Backup written to " + sdContacts.toString();
			        MainActivity.displayDialog(msg);
			        
			        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			        // Vibrate for 500 milliseconds
			        v.vibrate(500);
		        }

			}

		};

	    mThread.start();		
	}
	
	public void backupMessages(){
		MainActivity.mProgressDialog.setMessage("Backing up messages");
		MainActivity.mProgressDialog.setTitle("Please Wait...");
		MainActivity.mProgressDialog.setCancelable(false);
		MainActivity.mProgressDialog.show();
		
		Thread mThread = new Thread(){
			public void run(){
				Date d = new Date(System.currentTimeMillis());
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		        smsFile = "Messages_"+sdf.format(d).toString()+".xml";							
				
				if(writeMessageBackup()){
					MainActivity.mProgressDialog.dismiss();
					
					String msg = "Messages Backup written to " + sdMessages.toString();
				    
					MainActivity.displayDialog(msg);	
					
			        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			        // Vibrate for 500 milliseconds
			        v.vibrate(500);							
				}
						
			}
		};
		mThread.start();		
	}
	         	
	public void saveToDrive(){
			Intent intent = new Intent(mActivity, FileExplorer.class);
			intent.putExtra("whoCalled", "BackupFragment.saveToDrive");
			startActivity(intent);			
	}
	
	private boolean writeMessageBackup() {
		if(!(sdMessages.mkdirs() || sdMessages.isDirectory())){
			MainActivity.mProgressDialog.dismiss();
			MainActivity.displayDialog("Unable to create file");
			return false;
		}
		
							
		Cursor cursor = getActivity().getContentResolver()
				.query(Uri.parse("content://sms/"), null, null, null, null);
		
		int maxCount = cursor.getCount();
		MainActivity.mProgressDialog.setMax(maxCount);	
		
		String path = sdMessages.toString()+"/"+smsFile;
		
		try{
			mFileOutputStream  = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(mFileOutputStream, "UTF-8");
			
			mFileOutputStream
			.write("<?xml version=\"1.0\" ?>\n".getBytes());
			String smses = "<smses count=\""+cursor.getCount()+"\">\n";
			osw.append(smses);
			

			int count = 0;
			if(cursor.moveToFirst()){
				do{
					String sms = "   <sms";
					
					for(int i=0; i<cursor.getColumnCount(); i++){
						if(cursor.getColumnName(i).equals("address")
							 ||cursor.getColumnName(i).equals("body")
							 ||cursor.getColumnName(i).equals("type")
							 ||cursor.getColumnName(i).equals("date")
							 ||cursor.getColumnName(i).equals("read")){
							
							String s = cursor.getString(i);
							
							if(s.contains("&")){
								s = s.replace("&", "&amp;");
							}
							if(s.contains("<")){
								s = s.replace("<", "&lt;");
							}
							if(s.contains(">")){
								s = s.replace(">", "&gt;");
							}
							if(s.contains("\"")){
								s = s.replace("\"", "&quot;");
							} 
							if(s.contains("'")){
								s = s.replace("'", "&apos;");
							}
							
							sms = sms + " "+cursor.getColumnName(i)+"=\""
									+s+"\"";
						}
						
					}
					
					sms = sms + "/>\n";
					
					Log.v("sms", sms);
					
					osw.append(sms);
					
					MainActivity.mProgressDialog.setProgress(count++);
				}while(cursor.moveToNext());
			}	
			osw.append("</smses>");
			osw.close();
			
			DOP.Update(DOP, settingsDBTable.MESSAGES_BACKUP_PATH, path);
			
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
    private boolean getVcardString() {
        vCard = new ArrayList<String>();
        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
       
		if(!(sdContacts.mkdirs() || sdContacts.isDirectory())){
			MainActivity.mProgressDialog.dismiss();
			MainActivity.displayDialog("Unable to create file");
			return false;
		}			        
	    String storage_path = sdContacts.toString()+"/"+vfile;
	   
        try {
			mFileOutputStream = new FileOutputStream(sdContacts.toString()+"/"+vfile, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        int maxCount = cursor.getCount();
        MainActivity.mProgressDialog.setMax(maxCount);
        
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            for(int i =0;i<maxCount;i++)
            {

                get(cursor);
                Log.d("TAG", "Contact "+(i+1)+"VcF String is"+vCard.get(i));
                cursor.moveToNext();
                
                MainActivity.mProgressDialog.setProgress(i);
            }
            
        }
        else
        {
            Log.d("TAG", "No Contacts in Your Phone");
        }
        
        DOP.Update(DOP, settingsDBTable.CONTACTS_BACKUP_PATH, storage_path);
        
        return true;

    }    
    
    
	private void get(Cursor cursor) {
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor fd;
        try {
            fd = getActivity().getContentResolver().openAssetFileDescriptor(uri, "r");

            FileInputStream fis = fd.createInputStream();
            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            fis.read(buf);
            String vcardstring= new String(buf);
            vCard.add(vcardstring);

            mFileOutputStream.write(vcardstring.toString().getBytes());
        } catch (Exception e1) 
        {
            e1.printStackTrace();
        }
    }
	
	public void saveToDriveFromPath(String path){
		Uri uploadUri = Uri.fromFile(new File(path));
		
        Intent uploadIntent = ShareCompat.IntentBuilder.from(getActivity())
        .setText("Share Document")
        .setType("application/*")
        .setStream(uploadUri)
        .getIntent()
        .setPackage("com.google.android.apps.docs");
        
        startActivity(uploadIntent);    
	}

}
