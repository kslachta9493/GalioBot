package GalioBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class ContentDisplay {
	
	public static void checkForDownload(String content, MessageCreateEvent event) {
		
		if (!Download.validUrl(content)) {
			return;
		}
		
		if (checkFor(content, "reddit")) {
			String downloadurl = getDownloadURLFromJSON(trimRedditURL(content) + ".json");
			
			if (downloadurl == null) {
				return;
			}
			downloadurl = downloadurl.replace("\"", "");
			String downloadfilename = getDownloadFilename(downloadurl);
			
			if (Download.isVideoType(downloadurl)) {
				getFileUnderSize(downloadurl, downloadfilename, Download.downloadloc, 8000000);
				Download.download(downloadurl, downloadfilename, Download.downloadloc);
			} else {
				String holder = downloadurl;
				event.getMessage().getChannel().flatMap(channel -> channel.createMessage(holder)).subscribe();
			}
		}
	}
	
	private static void getFileUnderSize(String downloadurl, String downloadfilename, String directory, long size) {
		downloadurl = downloadurl.replace("\"", "");
	
		Download.download(downloadurl, downloadfilename, directory);
		
		
		if (getFileSize(downloadfilename, directory) > size) {
			downloadurl = downloadurl.replace("1080", "720");
			downloadurl = downloadurl.replace("360", "180");
			downloadurl = downloadurl.replace("720", "360");
			getFileUnderSize(downloadurl, downloadfilename, Download.downloadloc, size);
		}
		
	}
	
	private static long getFileSize(String downloadfilename, String directory) {
		File targetDir=new File(directory);
		File targetFile=new File(targetDir, downloadfilename);
		return FileUtils.sizeOf(targetFile);
	}
	private static String getDownloadFilename(String downloadurl) {
		String downloadfilename = "";
		
		for (String s: Download.videotype) {
			if (downloadurl.contains(s)) {
				downloadfilename = RandomUtilities.getTimeStamp() + s;
			}
		}
		
		return downloadfilename;
	}
	
	private static boolean checkFor(String content, String compare) {
		
		if (content.contains(compare)) {
			return true;
		}
		return false;
	}
	
	private static String getDownloadURLFromJSON(String url) {
		
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
	        String inputLine, output;
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
		        				if (inner.getAsJsonObject("data").get("secure_media").isJsonArray()) {
			        				if (inner.getAsJsonObject("data").getAsJsonObject("secure_media").has("reddit_video")) {
			        					output = inner.getAsJsonObject("data").getAsJsonObject("secure_media").getAsJsonObject("reddit_video").get("fallback_url").toString();
			        					if (Download.isVideoType(output)) {
			        						return output;
			        					}
			        				}
		        				} else {
		        					if (!inner.getAsJsonObject("data").get("secure_media").isJsonNull()) {
		        						if (inner.getAsJsonObject("data").getAsJsonObject("secure_media").has("reddit_video")) {
		        							output = inner.getAsJsonObject("data").getAsJsonObject("secure_media").getAsJsonObject("reddit_video").get("fallback_url").toString();
		        							if (Download.isVideoType(output)) {
				        						return output;
				        					}
		        						}
		        					}
		        				}
		        			}
		        			//Has video
		        			if (inner.getAsJsonObject("data").has("preview")) {
			        				if (inner.getAsJsonObject("data").getAsJsonObject("preview").has("reddit_video_preview")) {		
			        					return inner.getAsJsonObject("data").getAsJsonObject("preview").getAsJsonObject("reddit_video_preview").get("fallback_url").toString();
			        				}
			        		} 
		        			//has picture
		        			if (inner.getAsJsonObject("data").has("url_overridden_by_dest")) {
		        				return inner.getAsJsonObject("data").get("url_overridden_by_dest").toString();
		        			}
		        		}
	        		}
	        	}
	        	
	        }

	        in.close();
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return null;
	}
	
	private static String trimRedditURL(String url) {
		
		//https://www.reddit.com/r/HolUp/comments/p7f3k2/while_i_slowly_have_a_melt_down/?utm_medium=android_app&utm_source=share   
		return url.substring(0, StringUtils.ordinalIndexOf(url, "/", 8));
	}
}
