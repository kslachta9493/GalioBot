package GalioBot;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;

public final class Tarkov {

	private static String reserve = "[Reserve Map](https://mapgenie.io/tarkov/maps/reserve)\n"
			+ "[Grenade Launcher Distances](https://i.redd.it/0xi9ac96pst41.jpg)\n"
			+ "";
	private static String customs = "[Customs Map](https://mapgenie.io/tarkov/maps/customs)\n"
			+ "";
	private static String woods = "[Woods Map](https://mapgenie.io/tarkov/maps/woods)\n"
			+ "";
	private static String interchange = "[Interchange Map](https://mapgenie.io/tarkov/maps/interchange)\n"
			+ "[Interchange 40k](https://i.redd.it/0vmgol467cm41.jpg)\n"
			+ "";
	private static String labs = "[Labs Map](https://mapgenie.io/tarkov/maps/lab)\n"
			+ "";
	private static String factory = "[Factory Map](https://mapgenie.io/tarkov/maps/factory)\n"
			+ "";
	private static String shoreline = "[Shoreline Map](https://mapgenie.io/tarkov/maps/shoreline)\n"
			+ "";
	public static void sendTarkovRandom(MessageChannel c, String[] args) {
		int iterations = 100000;
		List<Map> maps = new ArrayList<Map>();
		Map labmap = new Map("Labs", labs, "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/d/d7/TheLabBanner.png/revision/latest?cb=20181225171705");
		Map woodmap = new Map("Woods", woods, "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/3/3e/Banner_woods.png/revision/latest?cb=20171101223132");
		maps.add(new Map("Customs", customs, "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/9/9f/Customs_Banner.png/revision/latest?cb=20200811151055"));
		maps.add(new Map("Factory", factory, "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/1/1a/Factory-Day_Banner.png/revision/latest?cb=20200811153020"));
		maps.add(new Map("Reserve", reserve, "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/f/f4/Reserve.png/revision/latest?cb=20191101214624"));
		maps.add(new Map("Shoreline", shoreline, "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/d/d5/Banner_shoreline.png/revision/latest?cb=20171101223501"));
		maps.add(new Map("Interchange", interchange, "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/3/3e/Banner_interchange.png/revision/latest?cb=20200811153253"));
		for (String s: args) {
			if (s.contains("lab")) {
				maps.add(labmap);
			}
			if (s.contains("wood")) {
				maps.add(woodmap);
			}
		}
		for (int i = 0; i < iterations; i++) {
			int j = RandomUtilities.randInt(0, maps.size());
			maps.get(j).increment();;
		}
		
		if (!maps.contains(labmap)) {
			maps.add(labmap);
		}
		if (!maps.contains(woodmap)) {
			maps.add(woodmap);
		}
		Collections.sort(maps);
		c.createEmbed(spec -> 
		  spec.setColor(Color.BLACK)
		    .setTitle(maps.get(0).getMap())
		    .setImage(maps.get(0).getSplash())
		    .addField("Links", maps.get(0).getData(), true)
		    .setFooter(getFooterMaps(maps), null)
		    .setTimestamp(Instant.now())
		).subscribe();

	}
	
	public static String getFooterMaps(List<Map> maps) {
		String output = "";
		for (Map m: maps) {
			output += m.getMap() + ":" + m.getCount() + "   ";
		}
		return output;
	}
	public static void sendGrenadeLauncher(MessageChannel c) {
		c.createMessage("https://i.redd.it/0xi9ac96pst41.jpg").subscribe();
	}
	public static void sendUsefulLinks(MessageChannel c) {
		
		c.createEmbed(spec -> 
		  spec.setColor(Color.BLACK)
		    .setTitle("Tarkov Links")
		    .setThumbnail("https://pngimg.com/uploads/escape_from_tarkov/escape_from_tarkov_PNG17.png")
		    .addField("Reserve", reserve, true)
		    .addField("Customs", customs, true)
		    .addField("Interchange", interchange, true)
		    .addField("Factory", factory, true)
		    .addField("Woods", woods, true)
		    .addField("Labs", labs, true)
		    .addField("Shoreline", shoreline, true)
		    .setTimestamp(Instant.now())
		).subscribe();
		
	}
	/*
	 * meds
	 */
	
	public static void sendRandomTarkovGear(MessageChannel c, String[] args) {
		

		String helmet, armor, rig, headset, backpack, weapon, secondary, pistol;
		
		
		for (String s: args) {
			switch(s)
	        {
	            case "helm":
	            	c.createMessage(getItem(parseTxt("helmets.txt"))).subscribe();
	            	return;
	            case "headset":
	            	c.createMessage(getItem(parseTxt("headset.txt"))).subscribe();
	            	return;
	            case "arig":
	            	c.createMessage(getItem(parseTxt("armorrig.txt"))).subscribe();
	            	return;
	            case "rig":
	            	c.createMessage(getItem(parseTxt("rig.txt"))).subscribe();
	            	return;
	            case "armor":
	            	c.createMessage(getItem(parseTxt("chestarmor.txt"))).subscribe();
	            	return;
	            case "backpack":
	            	c.createMessage(getItem(parseTxt("backpack.txt"))).subscribe();
	            	return;
	            case "weapon":
	            	c.createMessage(getItem(parseTxt("weapon.txt"))).subscribe();
	            	return;
	            case "pistol":	            	
	            	c.createMessage(getItem(parseTxt("pistols.txt"))).subscribe();
	            	return;
	            case "help":
	            	c.createEmbed(spec -> 
	            	spec.setColor(Color.BLACK)
	            	.addField("flags", "arig\n armor\n backpack\n headset\n helm\n rig\n pistol\n weapon\n", true)
	            	).subscribe();
	            	return;
	        }
		}
		int headsetchance = 60;
		int rigchance = 60;
		int secondarychance = 10;
		int pistolchance = 15;
		//helmet choice
		if (RandomUtilities.randInt(0, 100) < headsetchance) {
			helmet = getItem(parseTxt("helmets.txt"));
			headset = getItem(parseTxt("headset.txt"));
		} else {
			helmet = getItem(parseTxt("helmetnoheadphone.txt"));
			headset = "Not Useable";
		}
		
		if (RandomUtilities.randInt(0, 100) < rigchance) {
			armor = getItem(parseTxt("chestarmor.txt"));
			rig = getItem(parseTxt("rig.txt"));
		} else {
			armor = "Not Useable";
			rig = getItem(parseTxt("armorrig.txt"));
		}
		backpack = getItem(parseTxt("backpack.txt"));
		
		if (RandomUtilities.randInt(0, 100) < pistolchance) {
			pistol = getItem(parseTxt("pistol.txt"));
			weapon = "Empty";
			secondary = "Empty";
		} else {
			if (RandomUtilities.randInt(0, 100) < secondarychance) {
				weapon = getItem(parseTxt("weapon.txt"));
				secondary = getItem(parseTxt("weapon.txt"));
				pistol = "Empty";
			} else {
				weapon = getItem(parseTxt("weapon.txt"));
				secondary = "Empty";
				pistol = "Empty";
			}
		}
		
		c.createEmbed(spec -> 
			spec.setColor(Color.GREEN)
			.addField("Helmet", helmet, true)
			.addField("Headset", headset, true)
			.addField("Chest Armor", armor, true)
			.addField("Rig", rig, true)
			.addField("Backpack", backpack, true)
			.addField("Weapon", weapon, true)
			.addField("Secondary", secondary, true)
			.addField("Pistol", pistol, true)
			.addField("Trader Level", String.valueOf(RandomUtilities.randInt(1, 4)), true)
		).subscribe();
	}	
	
	private static String getItem(Set<String> myset) {

		int chance = RandomUtilities.randInt(0, 100);
		if (chance <= 2) {
			return "Empty";
		} else if (chance <= 4) {
			return "Player Choice";
		}
		return(String) myset.toArray()[RandomUtilities.randInt(0, myset.size())];
	}
	private static Set<String> parseTxt(String filename) {
		Set<String> output = new HashSet<String>();
		try {
		      File myObj = new File("tarkovgear\\" + filename);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        output.add(myReader.nextLine());
		      }
		      myReader.close();
		    } catch (Exception e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		return output;
	}
}
