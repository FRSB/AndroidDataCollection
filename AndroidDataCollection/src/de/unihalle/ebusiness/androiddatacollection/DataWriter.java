package de.unihalle.ebusiness.androiddatacollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;

public class DataWriter {
	private BufferedWriter bufferedWriter;
	private File path;
	private File file;
	private String headline;
	
	public DataWriter(String headline) {
		this.headline = headline;
		openWriter();
	}
	
	public void openWriter() {		
		try {
    		if (Environment.getExternalStorageState().equals("mounted")) {
				//remember .nomedia
	    		path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	    		path.mkdirs();
	    		file = new File(path, "data" + ".csv");
	    		bufferedWriter = new BufferedWriter(new FileWriter(file, true));	    		
	    		
	    		if (file.length() <= 10) {
		    		bufferedWriter.write(headline + "\n");
	    		}
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public void writeToFile(String string) {
		try {
			if (file.exists()) {
				bufferedWriter.write(string + "\n");
			} else {
				openWriter();
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public void emptyFile() {
		try {
			if (file.exists() & Environment.getExternalStorageState().equals("mounted")) {
				file.delete();
				openWriter();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeWriter() {
		try {
			bufferedWriter.flush();
			bufferedWriter.close();		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
