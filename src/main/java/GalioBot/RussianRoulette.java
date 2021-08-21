package GalioBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.reaction.ReactionEmoji;


public final class RussianRoulette {
	private static int deathslot = 0;
	private static int currentChamber = 0;
	private static List<String> participants = new LinkedList<String>();
	//public static List<String> commands = new LinkedList<String>();
	public static ArrayList<String> commands = new ArrayList<>(Arrays.asList("join", "start", "trigger", "spin"));
	private static BotUtilities bot;
	private static boolean initialized = false;
	private static boolean started = false;
	private static ButtonInteractEvent eventid;
	private static Snowflake startid;
	private static List<Snowflake> delete = new LinkedList<Snowflake>();
	private static int currentPlayer = 0;

	private RussianRoulette() {

	}

	public static void startGame() {
		deathslot = selectDeathSlot();
		started = true;
		currentPlayer = 0;
		sendGameStart();
		deleteStart();
	}
	
	public static void endGame() {
		participants.clear();
		started = false;
		deleteTrigger();
	}
	private static int selectDeathSlot() {
		return RandomUtilities.randInt(0, 6);
	}
	
	public static void addParticipant(String participant) {
		participants.add(participant);
	}
	public static void removeParticipant(String participant) {
		participants.remove(participant);
	}
	
	public static boolean triggerPull() {
		System.out.println("CC:" + currentChamber + " DS:" + deathslot);
		if (currentChamber == deathslot) {
			deathslot = selectDeathSlot();
			return true;
		}
		currentChamber++;
		if (currentChamber >= 6) {
			currentChamber = 0;
		}
		return false;
	}
	public static void spinChamber() {
		currentChamber = selectDeathSlot();
	}
	
	public static void parseInput(String input, String user, ButtonInteractEvent event) {
		eventid = event;
		
		if (input.equals("join")) {
			addParticipant(user);
			replyEphemeral(event, "You've successfully joined");
		}
		if (input.equals("start")) {
			if (!checkStart()) {
				replyEphemeral(event, "Game is now starting");
				startGame();
			} else {
				//sendMessage();
			}
			
		}
		if (checkStart()) {
			if (user.equals(getCurrentPlayer())) {
				if (!participants.contains(user)) {
					replyEphemeral(event, "You have not joined this game!");
					return;
				}
				if (input.equals("trigger")) {
					if (triggerPull()) {
						replyEphemeral(event, "You died!");
						removeParticipant(user);
					} else {
						replyEphemeral(event, "You survived!");
					}
					currentPlayer++;
					
					if (currentPlayer >= participants.size()) {
						currentPlayer = 0;
					}
				}
				if (input.equals("spin")) {
					spinChamber();
					replyEphemeral(event, "Chamber has been spun");
				}
			} else {
				replyEphemeral(event, "It's not your turn!");
			}
		}
		if (participants.isEmpty()) {
			System.out.println("Game Over Man");
			endGame();
			//delete start and join messages
		}
	}
	
	private static String getCurrentPlayer() {
		return participants.get(currentPlayer);
	}
	private static boolean checkStart() {
		return started;
	}
	public static boolean isInit() {
		return initialized;
	}
	
	public static void init(ButtonInteractEvent event) {
		bot = new BotUtilities();
		eventid = event;
		bot.setClient(event.getClient());
		initialized = true;
	}
	/*
	private static void sendMessage(String message) {
		
	}
	*/
	private static void sendGameStart() {		
		
		eventid.getInteraction().getChannel()
		.flatMap(channel -> channel.createMessage(msg -> {
			msg.setContent(getUserString());
			msg.setComponents(
					 ActionRow.of(
                             //              ID,  label
                             Button.primary("trigger", ReactionEmoji.codepoints("U+1F52B")),
                             Button.primary("spin", "Spin Chamber")
                     )
					);
		}))
		.map(message -> message.getId())
		.map(id -> delete.add(id)).subscribe();
	}
	
	private static String getUserString() {
		String output = "```Players:\n";
		int i = 1;
		for (String s: participants) {
			output += i + ": " + s + "\n";
			i++;
		}
		output += "```";
		return output;
	}
	private static void replyEphemeral(ButtonInteractEvent event, String output) {
		event.replyEphemeral(output).subscribe();
	}
	public static boolean sendId(Snowflake s) {
		startid = s;
		return true;
	}
	private static void deleteTrigger() {
		for (Snowflake s: delete) {
			eventid.getInteraction().getChannel()
			.flatMap(channel -> channel.getMessageById(s))
			.flatMap(message -> message.delete())
			.subscribe();
		}
	}
	private static void deleteStart() {
		eventid.getInteraction().getChannel()
		.flatMap(channel -> channel.getMessageById(startid))
		.flatMap(message -> message.delete())
		.subscribe();
	}
}
