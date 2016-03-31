package com.example.backup_and_restore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.example.backup_and_restore.R;
import com.example.backup_and_restore.settingsTableInfo.settingsDBTable;
import com.example.backup_and_restore.DatabaseOperations;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    static String vfile;
    static ArrayList<String> vCard;
    static Cursor cursor;
    
    static Context context;
    
    static FileOutputStream mFileOutputStream;
    
    static ProgressDialog mProgressDialog;

    static Handler mHandler;
    
    static int width, height;
    
    static BackupFragment backupFr;
    static RestoreFragment restoreFr;
        
    DatabaseOperations DOP;
    static String defaultPath;
    
    String fromWhere;
        
    @SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        width = p.x;
        height = p.y;
        
        Bundle bn = getIntent().getExtras();
        if(bn!=null){
            fromWhere = bn.getString("whoCalled");
        }

        defaultPath = Environment.getExternalStorageDirectory().toString() + "/backup_and_restore/";
        Log.d("defaultPath", defaultPath);
        DOP = new DatabaseOperations(getApplicationContext());
        
        mHandler = new Handler(Looper.getMainLooper()){

        	@Override
        	public void handleMessage(Message message) {
        		final String s = message.getData().getString("dialog_msg");
				new AlertDialog.Builder(context)
		           .setMessage(s)
		           .setCancelable(true)
		           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		               @SuppressLint("NewApi") public void onClick(DialogInterface dialog, int id) {

							Cursor cr = DOP.getInfo(DOP);
							if(cr.moveToFirst()){
								if(cr.getString(settingsDBTable.SAVE_TO_DRIVE_COLUMN)
										.equals(settingsDBTable.DRIVE_CHECKED)){
									if(s.startsWith("Contacts")){
										backupFr
										.saveToDriveFromPath(
										cr.getString(settingsDBTable.CONTACTS_BACKUP_PATH_COLUMN));
									}else if(s.startsWith("Messages")){
										backupFr
										.saveToDriveFromPath(
										cr.getString(settingsDBTable.MESSAGES_BACKUP_COLUMN));										
									}
								}
							}		            	   
		               }
		           })
		           .show();	        	
            }        	
        };
        
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        
        // Create the adapter that will return a fragment for each of the primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        context = MainActivity.this;
        
        if(fromWhere==null){
            // Get the intent that started this activity
            Intent intent = getIntent();
            Uri data = intent.getData();
            
            if (intent.getType()!=null 
            		&& intent.getType().equals("text/xml")) {
            	new RestoreFragment().restoreMessagesFromPath(data.getPath());
            }
        }else{
        	if(fromWhere!=null){
        		if(fromWhere.equals("RestoreFragment.restoreContacts")){
    				restoreFr.restoreContactsFromPath(getIntent().getData().getPath());      			
        		}else if(fromWhere.equals("RestoreFragment.restoreMessages")){
        			restoreFr.restoreMessagesFromPath(getIntent().getData().getPath());
        		}else if(fromWhere.equals("BackupFragment.saveToDrive")){
        			backupFr.saveToDriveFromPath(getIntent().getData().getPath());
        		}
        		
        	}        	
        }
    }
	 	
    
    @Override
    public void onResume(){
    	super.onResume();

    	Cursor cr = DOP.getInfo(DOP);
    	if(cr.moveToFirst()){
    		if(cr.getString(settingsDBTable.PATH_TYPE_COLUMN)
    				.equals(settingsDBTable.PATH_TYPE_DEFAULT)){
        		BackupFragment.sdContacts = new File(defaultPath+"/Contacts/");
        		BackupFragment.sdMessages = new File(defaultPath+"/Messages/") ;    			
    		}else{
        		BackupFragment.sdContacts = new File(
        				cr.getString(settingsDBTable.PATH_COLUMN)
        				.toString()+"/Contacts/") ;
        		BackupFragment.sdMessages = new File(
        				cr.getString(settingsDBTable.PATH_COLUMN)
        				.toString()+"/Messages/") ;   		
    		}
    	}
    	
    	
    	
    	
    	
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                	backupFr = new BackupFragment();
                	return backupFr;
                    
                default:
                	restoreFr = new RestoreFragment();
                	return restoreFr;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	switch(position){
        	case 0:
        		return "Backup";
        	case 1:
        		return "Restore";
        		default:
        			return "";
        	}
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //menu.getItem(0).setIcon(R.drawable.settings);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.action_settings:
    		Intent intent = new Intent(getApplicationContext(), Settings.class);
    		startActivity(intent);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    public static void showToast(String s){
    	Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
    
    public static void displayDialog(String msg){
		Message message = mHandler.obtainMessage();
	    Bundle b = new Bundle();
	    b.putString("dialog_msg", msg);
	    message.setData(b);
	    message.sendToTarget();	   	
    }
    
    public void backupContacts(View v){
    	backupFr.backupContacts();
    }
    
    public void backupMessages(View v){
    	backupFr.backupMessages();
    }
    
    public void saveToDrive(View v){
    	backupFr.saveToDrive();
    }
    
    public void restoreContacts(View v){
    	restoreFr.restoreContacts();
    }
    
    public void restoreMessages(View v){
    	restoreFr.restoreMessages();
    }
    
    public void retrieveFromDrive(View v){
    	restoreFr.retrieveFromDrive();
    }

}
