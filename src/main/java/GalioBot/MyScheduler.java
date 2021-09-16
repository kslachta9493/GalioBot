package GalioBot;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;

import discord4j.core.GatewayDiscordClient;

public class MyScheduler extends TimerTask {
	private BotUtilities bot;
	private String newname;
	private Channels channels;
	private GatewayDiscordClient client;
	
	public MyScheduler(GatewayDiscordClient client) {
		this.client = client;
		setup();
		cleanup();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("Channel Name changed to: " + changeChannel());
	}

	private void setup() {
		bot = new BotUtilities();
		channels = new Channels();
		bot.setClient(client);
	}
	public String changeChannel() {
		
		newname = channels.getChannelNameDay();
		
		bot.changeChannelName(newname);
		return newname;
	}
	
	private void cleanup() {
		File dr = new File(Download.downloadloc);
		//System.out.println(dr.getAbsolutePath());
		deleteDirectory(dr);
		File ir = new File(Download.imageloc);
		
		//check IR size and delete if too large
		if (FileUtils.sizeOfDirectory(ir) > 100000000) {
			deleteDirectory(ir);
		}
		System.out.println("Files Cleaned");
	}
	
	private void deleteDirectory(File f) {
		try {
			FileUtils.cleanDirectory(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
