package com.example.backup_and_restore;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FileExplorerAdapter extends ArrayAdapter<FileNameModel> {

	Activity context;
	ArrayList<FileNameModel> fileExplorerArray;
	
	
	public FileExplorerAdapter(Activity context, ArrayList<FileNameModel> fileExplorerArray) {
		super(context, R.layout.file_list, R.id.listView1, fileExplorerArray);
		this.context = context;
		this.fileExplorerArray = fileExplorerArray;
	}
	
	static class ViewHolder{
		TextView fileNameString;
		ImageView icon;
	}
	
	public FileNameModel getItem(int position){
		return fileExplorerArray.get(position);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		FileNameModel Model = fileExplorerArray.get(position);
		if(convertView == null)
		{
			LayoutInflater inflater=context.getLayoutInflater();
			convertView=inflater.inflate(R.layout.file_explorer, null);

			holder = new ViewHolder();

			holder.fileNameString = (TextView) convertView.findViewById(R.id.file_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.image);

			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		
		holder.fileNameString.setText(Model.fileName);
		if(Model.file.isDirectory())
		{
			holder.icon.setBackgroundResource(R.drawable.folder_icon);
		}
		else
		{
			holder.icon.setBackgroundResource(R.drawable.document_generic);
		}

		return convertView;
	}

}
