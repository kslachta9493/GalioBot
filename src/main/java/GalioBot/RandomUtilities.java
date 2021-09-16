package GalioBot;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

import discord4j.core.event.domain.message.MessageCreateEvent;

public final class RandomUtilities {
	private RandomUtilities() {	}
	
	public static String decide(String[] args, int iterations) {
		
		if (args.length == 1) {
			return "no u";
		}
		
		Integer[] holder = new Integer[args.length];
		for (int i = 0; i < args.length; i++) {
			holder[i] = 0;
		}
		
		for (int i = 0; i < iterations; i++) {
			int j = randInt(1, args.length);
			holder[j]++;
		}
		int max = 0;
		for (int i = 0; i < args.length; i++) {
			if (holder[i] > holder[max]) {
				max = i;
			}
		}
		String output = "Total Iterations: " + iterations;
		for (int i = 1; i < args.length; i++) {
			output += " | ";
			if (i == max)
				output += "**" + args[i] + ":" + holder[i] + "**";
			else
				output += args[i] + ":" + holder[i];
		}
		return output;
		
	}
	
	public static int randInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}
	
	public static int roll() {
		return ThreadLocalRandom.current().nextInt(1, 6);
	}
	public static int roll(int max) {
		return ThreadLocalRandom.current().nextInt(1, max);
	}
	
	public static String dallen() {
		String[] dallenemote = new String[] {"chicken", "uuurgh", "blaaagch", "bwaaap"};
		return dallenemote[randInt(0, dallenemote.length)];
	}
	
	public static int parseForInt(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		
		int max;
		if (args.length == 1) {
			max = 7;
		} else {
			max = Integer.parseInt(args[1]) + 1;
			System.out.println(max);
		}
		return max;
	}
	
	public static String getNameMeme(MessageCreateEvent event) {
		int i;
		
		String username = event.getMessage().getUserData().username();
		String lovename = " , love " + username;
		String s = event.getMessage().getContent().split(" ")[0].substring(1);
		
		String words[] = new String[] {" ", "is extra handsome today", "is extra bad today", "is a fat bitch today",
				"looks like a downsyndrome triceratops", "probably shouldn't play league today", "has a bigger penis than Ron Jeremy",
				"is pretty sus", "prefers pineapple pizza", "is a #ProudBoy", "probably should play league today",
				"wants to play runescape", "sucks eggs"};
		
		String love[] =  new String[] {" ", "is extra handsome today", "has a bigger penis than Ron Jeremy", 
				"probably should play league today", "'s gonna love the way they look, " + username + " guarantees it!"};
		

		
		if (event.getMessage().getContent().contains("-love")) {
			i = randInt(1, love.length);
			return s + love[0] + love[i] + lovename;
		} else {
			if (randInt(1, 8) == 1) {
				i = randInt(1, love.length);
				if (i == 4) 
					return s + love[0] + love[i];
				else
					return s + love[0] + love[i] + lovename;
			} else {
				i = randInt(1, words.length);	
				return s + words[0] + words[i] + lovename;
			}	
		}
	}
	public static String getTarkovMap(int iterations) {
		String maps[] = new String[] {
				"Labs", "Customs", "Shoreline", "Interchange", "Reserve", "Factory"
		};
		int[] holder = new int[maps.length];
		for (int i = 0; i < iterations; i++) {
			int j = randInt(1, maps.length);
			holder[j]++;
		}
		int max = 0;
		for (int i = 0; i < maps.length; i++) {
			if (holder[i] > holder[max]) {
				max = i;
			}
		}
		String output = "Total Iterations: " + iterations;
		for (int i = 1; i < maps.length; i++) {
			output += " | ";
			if (i == max)
				output += "**" + maps[i] + ":" + holder[i] + "**";
			else
				output += maps[i] + ":" + holder[i];
		}
		return output;
	}
	
	public static String getWipeTime() {
		
		Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 3);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.set(Calendar.DAY_OF_MONTH, 30);
        date.set(Calendar.MONTH, Calendar.JUNE);
        date.set(Calendar.YEAR, 2021);
        
        Duration today = Duration.ofMillis(System.currentTimeMillis());
        Duration tomorrow = Duration.ofMillis(date.getTimeInMillis());
        
        Duration diff = tomorrow.minus(today);
        System.out.println("DEIFF" + diff.toSeconds());
        System.out.println(today);
        System.out.println(tomorrow);
        if (diff.toSeconds() <= 0) {
        	return "Wipe Started!!";
        }
		return String.format("%d:%02d:%02d", diff.toSeconds() / 3600, (diff.toSeconds() % 3600) / 60, (diff.toSeconds() % 60));
	}
	
	public static String getTimeStamp() {
		SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(System.currentTimeMillis());
		
		return formatter.format(date);
	}
}
