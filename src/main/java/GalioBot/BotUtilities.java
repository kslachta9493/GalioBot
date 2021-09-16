package GalioBot;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
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
                     )
					);
		}))
		.map(message -> messageset.add(message))
		.subscribe();
	}
	
	public void join(MessageCreateEvent event) {
		//add check to make sure user is connected to voice channel first
		Mono.justOrEmpty(event.getMember())
			    .flatMap(Member::getVoiceState)
			    .flatMap(VoiceState::getChannel)
			    .flatMap(channel -> channel.join(spec -> spec.setProvider(null)))
			    .subscribe();	
	}
	
	public void leave(MessageCreateEvent event) {
		//check if bot is connected
		//disconnect after certain amount of time?
		event.getClient().getMemberById(Snowflake.of("165202988795691008"), Snowflake.of("165202988795691008")).flatMap(member -> member.edit(usr -> usr.setNewVoiceChannel(null))).subscribe(null, error -> {
			System.out.println("Bot Disconnect Error");
		});
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
	
	public void sendCommandList(MessageChannel chan, Map<String, Command> commands) {
		
		String output = "";
		String comlist[] = new String[30];
		int count = 0;
        for (final Map.Entry<String, Command> entry : commands.entrySet()) {
        		comlist[count] = entry.getKey();
        		count++;
        }
        Arrays.sort(comlist, 0, count);
        for (int i = 0; i < count; i++) {
    		output = output + "!" + comlist[i] + "\n";
        }
        final String temp = output;
		chan.createEmbed(spec -> 
		  spec.setColor(Color.BLACK)
		    .setTitle("GalioBot")
		    .addField("Commands", temp, true)
		    .setTimestamp(Instant.now())
		).subscribe();
	}
	
	public String[] parseArgs(MessageCreateEvent event) {
		return event.getMessage().getContent().split(" ");
	}
}
