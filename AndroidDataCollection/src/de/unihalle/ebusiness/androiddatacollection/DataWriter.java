package de.unihalle.ebusiness.androiddatacollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class DataWriter {
	
	private BufferedWriter bufferedWriter;
	private File path;
	private File file;
	private Boolean fileExists;
	
	public void openWriter() {		
		try {
    		if (Environment.getExternalStorageState().equals("mounted")) {
				//remember .nomedia
	    		path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	    		path.mkdirs();
	    		file = new File(path, "test.csv");
	    		fileExists = file.exists();	    		

	    		bufferedWriter = new BufferedWriter(new FileWriter(file, true));
	    		bufferedWriter.write("id;acc");
	    		bufferedWriter.flush();
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeToFile(String string) {
		try {
			if (fileExists) {
				bufferedWriter.write(string);
				bufferedWriter.flush();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void emptyFile() {
		try {
			if (fileExists & Environment.getExternalStorageState().equals("mounted")) {
				file.delete();
				this.openWriter();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeWriter() {
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
