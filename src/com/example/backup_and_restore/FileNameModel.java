package com.example.backup_and_restore;

import java.io.File;

public class FileNameModel {
	File file;
	String fileName;
	
	public FileNameModel(){
		
	}
	
	public FileNameModel(File file, String fileName){
		this.file = file;
		this.fileName = fileName;
	}

}
