package GalioBot;

import java.util.TimerTask;

import discord4j.core.GatewayDiscordClient;

public class MyScheduler extends TimerTask {
	private BotUtilities bot;
	private String newname;
	private Channels channels;
	private GatewayDiscordClient client;
	
	public MyScheduler(GatewayDiscordClient client) {
		this.client = client;
		setup();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		changeChannel();
		System.out.println("I RAN");
	}

	private void setup() {
		bot = new BotUtilities();
		channels = new Channels();
		bot.setClient(client);
	}
	public void changeChannel() {
		
		newname = channels.getChannelNameDay();
		
		bot.changeChannelName(newname);
	}
}
