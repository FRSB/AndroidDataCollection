package de.unihalle.ebusiness.androiddatacollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import android.os.Environment;
import android.util.Log;

public class DataWriter {
	private BufferedWriter bufferedWriter;
	private File path;
	private File file;
	private String headline;
	private ZipFile zipFile;
	private ZipParameters parameters;
	
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
	    		file = new File(path, Long.toString(System.currentTimeMillis()) + ".csv");

	    		zipFile = new ZipFile(path + "/test.zip");
	    		
	    		bufferedWriter = new BufferedWriter(new FileWriter(file, true));
	    		
	    		parameters = new ZipParameters();
	    		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
	    		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
	    		
	    		if (file.canRead()) {
		    		bufferedWriter.write(headline + "\n");
		    		bufferedWriter.flush();
	    		}
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public void writeToFile(String string) {
		try {
			if (file.canRead()) {
				bufferedWriter.write(string + "\n");
			} else {
				openWriter();
			}
			
			if (file.length() > 8000) {
				zipFile.addFile(file, parameters);
				file.delete();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void emptyFile() {
		try {
			if (file.canRead() & Environment.getExternalStorageState().equals("mounted")) {
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
			zipFile.addFile(file, parameters);
			file.delete();
			Log.i("Storage", Boolean.toString(file.canRead()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
