package GalioBot;

import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Mono;

public class BotUtilities {
	
	private List<Snowflake> fullist = new LinkedList<Snowflake>();
	private Set<Message> messageset = new HashSet<Message>();
	GatewayDiscordClient client;
	
	public BotUtilities() {
	}
	
	public BotUtilities(GatewayDiscordClient client) {
		this.client = client;
	}
	
	public void setClient(GatewayDiscordClient client) {
		this.client = client;
	}
	public void sendMessage(MessageCreateEvent event, String output) {
		//fullist.add(event.getMessage().getId());		
		event.getMessage().getChannel()
		.flatMap(channel -> channel.createMessage(output))
		.map(message -> messageset.add(message))
		.subscribe();
	}
	
	public void sendMessageDelay(MessageCreateEvent event, String output) {
		event.getMessage().getChannel()
		.flatMap(channel -> channel.createMessage(output))
		.delayElement(Duration.ofSeconds(10))
		.flatMap(message -> message.delete())
		.then(Mono.just(event.getMessage()))
		.flatMap(message -> message.delete())
		.subscribe();
	}
	
	public void sendBotMessage(MessageCreateEvent event, String output, int delay) {
		event.getMessage().getChannel().block().createMessage(output)
		.delayElement(Duration.ofSeconds(delay))
		.flatMap(message -> message.delete())
		.subscribe();
	}
	
	public void cleanMessages(MessageCreateEvent event, String output) {
		/*
		for (Snowflake s: fullist) {
			event.getMessage().getChannel()
			.flatMap(channel -> channel.getMessageById(s))
			.flatMap(message -> message.delete())
			.subscribe();
		}
		*/
		//MessageBulkDeleteEvent mbde = new MessageBulkDeleteEvent(client, null, getLongs(), getChannelId(event), getGuildId(event), messageset);
		//event.getMessage().getChannel().cast(GuildMessageChannel.class).flux().flatMap(gmc -> gmc.bulkDelete(fullist));
		//.map(gmc -> gmc.bulkDelete(fullist));

		fullist.clear();
		messageset.clear();
		sendMessageDelay(event, output);
	}
	public void sendPrivateMessage(MessageCreateEvent event, String output) {
		event.getMember().map(member -> member.getPrivateChannel()
				.flatMap(pv -> pv.createMessage(output)).subscribe());
	}
	
	public void sendJohnBitch(MessageCreateEvent event, String output) {
		event.getGuild().flatMap(guild -> guild.getMemberById(Snowflake.of("302897776235249664"))
				.map(member -> member.getPrivateChannel().map(pc -> pc.createMessage(output).subscribe()).subscribe())).subscribe();
	}
	public void changeChannelName(MessageCreateEvent event, String newname) {
		event.getGuild().flatMap(guild -> guild.getChannels()
				.filter(guildChannel -> guildChannel.getType().equals(Channel.Type.GUILD_VOICE))
				.cast(VoiceChannel.class).next().flatMap(vc -> vc.edit(vces -> vces.setName(newname)))).subscribe();
	}
	public void changeChannelName(String newname) {	
		client.getGuilds().flatMap(guilds -> guilds.getChannels()
				.filter(guildChannel -> guildChannel.getType().equals(Channel.Type.GUILD_VOICE))
				.cast(VoiceChannel.class).next()
				.flatMap(vc -> vc.edit(vces -> vces.setName(newname)))).subscribe();
	}
	public void changeChannelName(MessageCreateEvent event, Snowflake channel, String newname) {
		event.getClient().getChannelById(channel)
		.cast(VoiceChannel.class)
		.flatMap(vc -> vc.edit(vces -> vces.setName(newname)))
		.subscribe();
	}
	
	public void addMessage(MessageCreateEvent event) {
		fullist.add(event.getMessage().getId());
		messageset.add(event.getMessage());
	}
		
	public void sendButtonMessage(MessageCreateEvent event, String buttonname, String buttonid, String content) {
		event.getMessage().getChannel()
		.flatMap(channel -> channel.createMessage(msg -> {
			msg.setContent(content);
			msg.setComponents(
					//ActionRow.of(
					//		Buttons.primary(buttonid, buttonname))
					 ActionRow.of(
                             //              ID,  label
                             Button.primary("one", "1"),
                             Button.primary("2", "2"),
                             Button.primary("3", "3"),
                             Button.primary("4", "4"),
                             Button.primary("5", ReactionEmoji.codepoints("U+1F44C"))
                     ),
                     ActionRow.of(
                             Button.primary("6", "6"),
                             Button.primary("7", "7"),
                             Button.primary("8", "8"),
                             Button.primary("9", "9"),
                             Button.primary("10", "10")
                     ),
                     ActionRow.of(
                             Button.primary("11", "11"),
                             Button.primary("12", "12"),
                             Button.primary("13", "13"),
                             Button.primary("14", "14"),
                             Button.primary("15", "15")
                     ),
                     ActionRow.of(
                             Button.primary("16", "16"),
                             Button.primary("17", "17"),
                             Button.primary("18", "18"),
                             Button.primary("19", "19"),
                             Button.primary("20", "20")
                     ),
                     ActionRow.of(
                             Button.primary("21", "21"),
                             Button.primary("22", "22"),
                             Button.primary("23", "23"),
                             Button.primary("24", "24"),
                             Button.primary("25", "25")
                     )
					);
		}))
		.map(message -> messageset.add(message))
		.subscribe();
	}
	public void sendButtonTest(MessageCreateEvent event) {
		event.getMessage().getChannel()
		.flatMap(channel -> channel.createMessage(msg -> {
			msg.setContent("Test Button");
			msg.setComponents(
					 ActionRow.of(
                             //              ID,  label
                             Button.primary("join", "Join"),
                             Button.primary("start", "Start Game")
                             //ReactionEmoji.codepoints("U+1F52B squirt gun
                     )
					);
		}))
		.map(message -> message.getId())
		.map(RussianRoulette::sendId)
		.subscribe();
	}
	
	public void join(MessageCreateEvent event) {

		Mono.justOrEmpty(event.getMember())
			    .flatMap(Member::getVoiceState)
			    .flatMap(VoiceState::getChannel)
			    .flatMap(channel -> channel.join(spec -> spec.setProvider(null)))
			    .subscribe();	

	}
	
	public void leave(MessageCreateEvent event) {
		Mono.justOrEmpty(event);
	}
	
	public void sendVoteButton(MessageCreateEvent event) {
		String user = event.getMessage().getUserData().username();
		event.getMessage().getChannel()
		.flatMap(channel -> channel.createMessage(msg -> {
			msg.setContent("Kick->" + user);
			msg.setComponents(
					 ActionRow.of(
                             //              ID,  label
                             Button.primary("kick", "Kick"),
                             Button.primary("nokick", "Don't Kick")
                     )
					);
		}))
		.subscribe();
	}
}
