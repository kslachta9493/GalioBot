package GalioBot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.apache.commons.validator.routines.UrlValidator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Download {
	public static final String downloadloc = "downloads";
	public static final String[] picturetype = new String[] {
			".png", ".jpg", ".jpeg", ".gif", "tiff"
	};
	public static final String[] videotype = new String[] {
			".mp4", ".mov", ".mmv", ".flv", "avi", "webm"
	};
	public Download() {
		
	}
	public static String download(String url) {
		
		String filename = "";
		
		for (String s: videotype) {
			if (url.contains(s)) {
				filename = getTimeStamp() + s;
			}
		}
		url = url.replace("\"", "");
		url = url.replace("1080", "720");
		File targetDir=new File(downloadloc);
		File targetFile=new File(targetDir, filename);
		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
				
					
					
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
		
			if (!checkFile(filename)) {
				url = url.replace("360", "180");
				url = url.replace("720", "360");
				return download(url);
			}
		
		/*
		 * Todo: Add filesize check and download worse version if needed
		 * parse string correctly to get rid of data
		 */
		return filename;
	}	
	
	public static String parseUrl(String url) {
		
		//https://www.reddit.com/r/HolUp/comments/p7f3k2/while_i_slowly_have_a_melt_down/?utm_medium=android_app&utm_source=share
		return url.substring(0, StringUtils.ordinalIndexOf(url, "/", 8));
	}
	private static boolean checkFile(String url) {
		
		//Path size = Paths.get(url);
		File targetDir=new File(downloadloc);
		File targetFile=new File(targetDir, url);
		long fileSize = FileUtils.sizeOf(targetFile);
		if (fileSize > 8000000) {
			return false;
		}
		return true;
	}
	private static String getTimeStamp() {
		SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(System.currentTimeMillis());
		
		return formatter.format(date);
	}
	
	public static String parse(String url) {
	
		JsonArray ja ;
		UrlValidator urlValidator = new UrlValidator();
	    if (!urlValidator.isValid(url)) {
	    	return null;
	    }
	    try {        
	        
	        URL urls = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urls.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("User-Agent", "GalioBot");
            connection.setRequestProperty("Accept", "application/json");

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {  
	        	ja = new JsonParser().parse(inputLine).getAsJsonArray();
	        	for (Object a: ja) {
	        		JsonObject test = (JsonObject) a;
	        		JsonArray data = test.getAsJsonObject("data").getAsJsonArray("children");
	        		
	        		if (data != null) {
		        		for (Object b : data) {
		        			JsonObject inner = (JsonObject) b;
		        			JsonArray innerdata;
		        			//crosspost
		        			if (inner.getAsJsonObject("data").has("crosspost_parent_list")) {
		        				innerdata = inner.getAsJsonObject("data").getAsJsonArray("crosspost_parent_list");
		        				
		        				if (innerdata != null) {
				        			for (Object c: innerdata) {
				        				JsonObject deep = (JsonObject) c;
					        			JsonObject deepdata = deep.getAsJsonObject("preview").getAsJsonObject("reddit_video_preview");
					        			System.out.println(deepdata.get("fallback_url").toString());
					        			return deepdata.get("fallback_url").toString();
				        			}
			        			}
		        			} 
		        			
		        			if (inner.getAsJsonObject("data").has("secure_media")) {
		        				if (inner.getAsJsonObject("data").getAsJsonObject("secure_media").has("reddit_video")) {
		        					return inner.getAsJsonObject("data").getAsJsonObject("secure_media").getAsJsonObject("reddit_video").get("fallback_url").toString();
		        				}
		        			}
		        			//Has video
		        			if (inner.getAsJsonObject("data").has("preview")) {
			        				if (inner.getAsJsonObject("data").getAsJsonObject("preview").has("reddit_video_preview"))
			        				return inner.getAsJsonObject("data").getAsJsonObject("preview").getAsJsonObject("reddit_video_preview").get("fallback_url").toString();
			        		} 
		        			//has picture
		        			if (inner.getAsJsonObject("data").has("url_overridden_by_dest")) {
		        				return inner.getAsJsonObject("data").get("url_overridden_by_dest").toString();
		        			}
		        			//Has video
		        			if (inner.getAsJsonObject("data").has("url")) {
		        				return inner.getAsJsonObject("data").get("url").toString();
		        			} 
		        		}
	        		}
	        	}
	        	
	        }

	        in.close();
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Failed");
	    }
	    System.out.println("IM NULL");
	    return null;
	}
	
	
	
}
