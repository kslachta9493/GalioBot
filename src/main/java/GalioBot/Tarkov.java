package GalioBot;

import java.time.Instant;

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
		//User[] values = (User[]) users.values().toArray(new User[0]);
		//Arrays.sort(values);
		int iterations = 100000;
		String maps[] = new String[] {
				"Labs", "Customs", "Shoreline", "Interchange", "Woods", "Reserve", "Factory"
		};
		int labson = 1;
		
		for (String s: args) {
			if (s.contains("lab")) {
				labson = 0;
			}
		}
		//customs image https://tarkov-tools.com/maps/customs.jpg
		//interchange https://tarkov-tools.com/maps/interchange.jpg
		int[] holder = new int[maps.length];
		for (int i = 0; i < iterations; i++) {
			int j = RandomUtilities.randInt(labson, maps.length);
			holder[j]++;
		}
		
		c.createEmbed(spec -> 
		  spec.setColor(Color.BLACK)
		    .setTitle("Tarkov Maps")
		    .addField(maps[1], String.valueOf(holder[1]), true)
		    .addField(maps[2], String.valueOf(holder[2]), true)
		    .addField(maps[3], String.valueOf(holder[3]), false)
		    .addField(maps[4], String.valueOf(holder[4]), true)
		    .addField(maps[5], String.valueOf(holder[5]), false)
		    .addField(maps[6], String.valueOf(holder[6]), true)
		    .setTimestamp(Instant.now())
		).subscribe();
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
	 * Helm
	 * Chest
	 * Skip rig if armored
	 * backpack
	 * gun
	 * pistol
	 * meds
	 */
	public static void getHelmet() {
		
	}
}
