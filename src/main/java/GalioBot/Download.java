package GalioBot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import discord4j.discordjson.json.AttachmentData;

import org.apache.commons.validator.routines.UrlValidator;

public class Download {
	public static final String downloadloc = "downloads";
	public static final String imageloc = "images";
	public static final String[] picturetype = new String[] {
			".png", ".jpg", ".jpeg", ".gif", "tiff"
	};
	public static final String[] videotype = new String[] {
			".mp4", ".mov", ".mmv", ".flv", "avi", "webm"
	};
	public Download() {
		
	}

	public static void download(String location, String output, String directory) {
			
		File targetDir=new File(directory);
		File targetFile=new File(targetDir, output);
		try (BufferedInputStream in = new BufferedInputStream(new URL(location).openStream());
				
					
					
				  FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
				    byte dataBuffer[] = new byte[1024];
				    int bytesRead;
				    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				        fileOutputStream.write(dataBuffer, 0, bytesRead);
				    }
				    fileOutputStream.flush();
				    fileOutputStream.close();
				} catch (IOException e) {
				    // handle exception
					e.printStackTrace();
				}
	}
		
	
	public static boolean isVideoType(String file) {
		
		for (String s: videotype) {
			if (file.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	
	public static boolean validUrl(String url) {
		UrlValidator urlValidator = new UrlValidator();
	    if (urlValidator.isValid(url)) {
	    	return true;
	    }
		return false;
	}
	
	public static void downloadImage(List<AttachmentData> list) {
		for (AttachmentData ad : list) {
			if (ad.url() != null) {
				download(ad.url(), ad.filename(), imageloc);
			}
		}
	}
}
