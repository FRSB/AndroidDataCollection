package de.unihalle.ebusiness.androiddatacollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.zip.GZIPOutputStream;
import android.os.Environment;

public class DataWriter {
	private BufferedWriter bufferedWriter;
	private File path;
	private File file;
	private File fileC;
	private String headline;
	private GZIPOutputStream gzipOutputStream;
	private FileOutputStream fileOutputStream;
	private int gzipBufferCounter = 0;
	private int gzipBufferSize = 16384;
	
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
	    		fileC = new File(path, "data" + ".csv.gz");
	    		fileOutputStream = new FileOutputStream(fileC, true);
	    		
	    		gzipOutputStream = new GZIPOutputStream(fileOutputStream, gzipBufferSize);
	    		bufferedWriter = new BufferedWriter(new FileWriter(file, true));	    		
	    		
	    		if (fileC.length() <= 20) {
		    		bufferedWriter.write(headline + "\n");
		    		gzipOutputStream.write((headline + "\n").getBytes());
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
				gzipBufferCounter++;
				bufferedWriter.write(string + "\n");
				gzipOutputStream.write((string + "\n").getBytes());	

			} else {
				openWriter();
			}
			
			if (gzipBufferCounter >= 60) {
				gzipOutputStream.finish();
				gzipOutputStream = new GZIPOutputStream(fileOutputStream, gzipBufferSize);
	    		gzipBufferCounter = 0;
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
			gzipOutputStream.finish();
			fileOutputStream.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
