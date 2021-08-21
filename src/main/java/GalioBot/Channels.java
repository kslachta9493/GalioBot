package GalioBot;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildChannel;
import reactor.core.publisher.Mono;

public class Channels {
	String channelname;
	
	Duration lastuse;
	Duration curruse;
	GatewayDiscordClient client;
	
	String[] monday = new String[] {"Mondays", "Montana", "Muscular", "Mayonnaise", "Minecraft"};
	String[] tuesday = new String[] {"Tuesdays", "Tuscany", "Taco", "Titillating", "Tiny Tit"};
	String[] wednesday = new String[] {"Wednesdays", "Wap", "White Girl"};
	String[] thursday = new String[] {"Thursdays", "Thrusting", "Thirsty", "Thunder", "Thonk"};
	String[] friday = new String[] {"Fridays","Feeding", "Fuckboi"};
	String[] saturday = new String[] {"Saturdays", "Tsunami", "Salami", "Salty"};
	String[] sunday = new String[] {"Sundays","Suck it", "Sim"};
	String[] command = new String[] {"","I'm not feeding", "Gumping It", "HEAL ME???", "Where's the ring, HELLO", "Hoes Mad, John's Bad",
									 "TF isn't a champion", "Traps aren't gay", "Motha fuckas look like catdog", "Red buff killed my pappy",
									 "Kevvvv","Traps are gay","Traps might be gay"};
	//Kev is number 11 on the list be sure to exclude this from the RNG
	String[][] all = new String[][] {sunday, monday, tuesday, wednesday, thursday, friday, saturday, command};
	
	public Channels(GatewayDiscordClient client) {
		this.client = client;
	}
	
	public Channels() {
		
	}
	
	public String getChannelNameDay() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		String[] curr = all[day - 1];
		
		int randomNum = ThreadLocalRandom.current().nextInt(1, curr.length);
		return curr[randomNum] + " " + curr[0];
	}
	
	public String getChannelNameMeme() {
		String[] curr = all[7];

		int randomNum = ThreadLocalRandom.current().nextInt(1, curr.length);
		return curr[randomNum];
	}
	
	public void respondToMessageDelay(MessageCreateEvent event, String output) {
		event.getMessage().getChannel()
		.flatMap(channel -> channel.createMessage(output))
		.delayElement(Duration.ofSeconds(10))
		.flatMap(message -> message.delete())
		.then(Mono.just(event.getMessage()))
		.flatMap(message -> message.delete())
		.subscribe();
	}
	
	public String getCurrentName(MessageCreateEvent event) {
		event.getClient().getChannelById(Snowflake.of("658835290001113098"))
		.cast(GuildChannel.class).map(vc -> vc.getName()).subscribe(value -> channelname = value);
		
		return channelname;
	}
	
	public boolean isReady() {
		
		if (lastuse == null) {
			lastuse = Duration.ofMillis(System.currentTimeMillis());
			return true;
		}
		curruse = Duration.ofMillis(System.currentTimeMillis());
		
		Duration diff = curruse.minus(lastuse); 
		
		if (diff.getSeconds() < 360) {
			return false;
		}
		lastuse = curruse;
		return true;
	}
	
	public String getTimeRemaining() {
		
		Duration diff = curruse.minus(lastuse);
		
		long uptime = 360 - diff.getSeconds();
		long minutes = TimeUnit.SECONDS.toMinutes(uptime);
		uptime -= TimeUnit.MINUTES.toSeconds(minutes);
		long seconds = TimeUnit.SECONDS.toSeconds(uptime);
		
		String output = "You must wait at least 6 minutes between changing voice channel names. You can use this command again in " + minutes + "m:" + seconds + "s";
		
		return output;
	}
}
