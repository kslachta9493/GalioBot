package GalioBot;

import discord4j.core.event.domain.interaction.ButtonInteractEvent;

public class ButtonHandler {
	private Logger log;
	public ButtonHandler(Logger log) {
		this.log = log;
		}
	
	public void handleEvent(ButtonInteractEvent event) {
		logButtonPush(event.getCustomId(), event.getInteraction().getUser().getTag());
		matchMethod(event.getCustomId(), event.getInteraction().getUser().getTag(), event);
	}
	
	private void logButtonPush(String buttonid, String user) {
		log.logTest(user + " pushed " + buttonid);
	}
	/*
	 * Useful method to match button ID to a method call
	private void findMethod(String value) {
		try {
			Method method = this.getClass().getDeclaredMethod(value);
			method.invoke(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	
	private void matchMethod(String value, String user, ButtonInteractEvent event) {
		if (VoteKick.commands.contains(value)) {
			VoteKick.parse(event);
		}
			else {
			event.replyEphemeral("You're a fat bitch").subscribe();
		}
	}
}
