package GalioBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.HashMap;


import java.util.Arrays;


import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;

public class Logger {
	private FileWriter out;
	private FileWriter voice;
	private String filename = "scoreboard";
	private HashMap<String, User> users = new HashMap<>();
	public Logger() {
		setupLogger();
	}

	public void logText(MessageCreateEvent event) {
		String timestamp = RandomUtilities.getTimeStamp();
		String username = event.getMessage().getUserData().username();
		String message = event.getMessage().getContent();
		try {
			out.write(timestamp + ": " + username + ": " + message + "\n");
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(timestamp + ": " + username + ": " + message);
	}
	
	private void writeToVoiceFile(String output) {
		try {
			voice.write(output);
			voice.flush();
		} catch (Exception e) {
			System.out.println("Write to file failed");
		}
	}
	public void logTest(String output) {
		String timestamp = RandomUtilities.getTimeStamp();
		
		try {
			out.write(timestamp + ": " + output + "\n");
			out.flush();
		} catch (Exception e) {
			System.out.println("Write to file failed");
		}
		System.out.println(timestamp + ": " + output);
	}
	
	public void logVoice(VoiceStateUpdateEvent event) {
		
		String timestamp = RandomUtilities.getTimeStamp();
		event.getCurrent().getMember().map(test -> test.getTag()).subscribe(name -> {					
			if (!users.containsKey(name)) {
				users.put(name, new User(name));
			}
		});
		if (event.isLeaveEvent()) {
			event.getOld().map(vc -> vc.getMember().map(user -> user.getTag()).subscribe(value -> {
				System.out.println(timestamp + " User:" + value + " disconnected!");
				writeToVoiceFile(timestamp + " User:" + value + " disconnected!\n");
			}));
		} else if (event.isJoinEvent()) {
			event.getCurrent().getChannel().map(channel -> channel.getName()).subscribe(value -> {
				event.getCurrent().getMember().map(test -> test.getTag()).subscribe(name -> {
					System.out.println(timestamp + " User:" + name + " joined channel:" + value);
					writeToVoiceFile(timestamp + " User:" + name + " joined channel:" + value + "\n");
				});
			});
			//give small percent chance to just get kicked
			
			if (RandomUtilities.randInt(0, 100) == 69) {
				System.out.println("User Kicked");
				event.getCurrent().getMember().map(test -> test.getTag()).subscribe(name -> {					
						users.get(name).addkick();
				});
				
				event.getCurrent().getMember().map(member -> member.edit(me -> me.setNewVoiceChannel(null)).subscribe()).subscribe();
			}
			/*
			event.getCurrent().getMember().map(test -> test.getTag()).subscribe(name -> {
				if (name.equals("Chainheal#2970")) {
					event.getCurrent().getMember().map(member -> member.edit(me -> me.setNewVoiceChannel(null)).subscribe()).subscribe();
				}
			});
			*/
		} else if (event.isMoveEvent()) {
			event.getOld().map(vc -> vc.getMember().map(user -> user.getTag()).subscribe(name -> {
				event.getOld().map(vco -> vco.getChannel().map(chan -> chan.getName()).subscribe(oldchan -> {
					event.getCurrent().getChannel().map(nc -> nc.getName()).subscribe(newchan -> {
						System.out.println(timestamp + " User:" + name + " moved from " + oldchan + " to " + newchan);
						writeToVoiceFile(timestamp + " User:" + name + " moved from " + oldchan + " to " + newchan + "\n");
					});
				}));
			}));
		}
		save();
	}
		
	@SuppressWarnings("unchecked")
	private void setupLogger() {
		try {
			File textLog = new File("textLog.txt");
			File voiceLog = new File("voiceLog.txt");
			voice = new FileWriter(voiceLog, Charset.forName("UTF16"), true);
			out = new FileWriter(textLog, Charset.forName("UTF16"), true);
			FileInputStream file;
			ObjectInputStream in;
			file = new FileInputStream(filename);
			in = new ObjectInputStream(file);
			users = (HashMap<String, User>) in.readObject();
			in.close();
			file.close();
		} catch (Exception e) {
			System.out.println("Failed to create file or filewriter");
		}
	}
	
	public void save() {
		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(users);
			out.close();
			file.close();
		} catch (Exception e) {
			
		}
	}
	
	public void sendScoreBoard(MessageChannel c) {
		User[] values = (User[]) users.values().toArray(new User[0]);
		Arrays.sort(values);
		c.createEmbed(spec -> 
		  spec.setColor(Color.BLACK)
		    .setTitle("Kick Scoreboard")
		    .addField("User", getUsernames(values), true)
		    .addField("Kicked", getValues(values), true)
		    .setTimestamp(Instant.now())
		).subscribe();
	}
	
	public String getUsernames(User[] values) {
		String output = "";
		
		int max = 5;
		if (values.length < 5) {
			max = values.length;
		}
		for (int i = 0; i < max; i++) {
			output += values[i].getUsername()  + "\n";
		}
		return output;
	}
	
	public String getValues(User[] values) {
		String output = "";
		
		int max = 5;
		if (values.length < 5) {
			max = values.length;
		}
		for (int i = 0; i < max; i++) {
			output += values[i].getKickCount() + "\n";
		}
		return output;
	}
}
