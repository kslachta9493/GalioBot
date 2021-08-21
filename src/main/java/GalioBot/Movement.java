package GalioBot;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;

public class Movement {
	private List<Snowflake> channelcodes;
	private List<VoiceState> voicestates;
	private Snowflake curruser;
	private Snowflake caller;
	private Snowflake afkchan;
	String holder = "";
	
	public Movement() {
			
	}
	
	public void returnChannelUser(MessageCreateEvent event) {
		
		getAllUsers(event);
		
		Snowflake ourchannel = randVoice(event);
		for (VoiceState s: voicestates) {
			specChanneluser(event, s.getUserId(), s.getGuildId(), ourchannel);
		}
	}
	
	private void specChanneluser(MessageCreateEvent event, Snowflake user, Snowflake guild, Snowflake ourchannel) {

		event.getClient().getMemberById(guild, user).flatMap(member -> member.edit(usr -> usr.setNewVoiceChannel(ourchannel))).subscribe(null, error -> {
			System.out.println("IT HAPPENED");
		});
	}
	
	private void randChanneluser(MessageCreateEvent event, Snowflake user, Snowflake guild) {
		Snowflake check = randVoice(event);

		event.getClient().getChannelById(check).cast(GuildChannel.class).map(channel -> channel.getName()).subscribe(value -> holder = value);
		event.getClient().getMemberById(guild, user).flatMap(member -> member.edit(usr -> usr.setNewVoiceChannel(check))).subscribe(null, error -> {
			System.out.println("IT HAPPENED");
		});
	}
	public void randomChannels(MessageCreateEvent event) {
		
		getAllUsers(event);
		for (VoiceState s: voicestates) {
			randChanneluser(event, s.getUserId(), s.getGuildId());
		}
	}
	private void getAllUsers(MessageCreateEvent event) {
		if (voicestates != null) {
			voicestates.clear();
		}
		
		event.getGuild().flatMap(guild -> guild.getVoiceStates()
				.collectList())
		.subscribe(value -> voicestates = value);
	}
	private Snowflake randVoice(MessageCreateEvent event) {
		
		if (channelcodes != null) {
			channelcodes.clear();
		}
		
		event.getGuild().flatMap(guild -> guild.getChannels()
				.filter(guildChannel -> guildChannel.getType().equals(Channel.Type.GUILD_VOICE))
				.map(channel -> channel.getId()).collectList()).subscribe(value -> channelcodes = value);
		
		removeAFKChannel(event);
		
		int randomNum = ThreadLocalRandom.current().nextInt(1, channelcodes.size() - 1);
		
		
		return channelcodes.get(randomNum);
	}

	private Snowflake randVoice(MessageCreateEvent event, Snowflake curr) {
		
		if (channelcodes != null) {
			channelcodes.clear();
		}
		
		event.getGuild().flatMap(guild -> guild.getChannels()
				.filter(guildChannel -> guildChannel.getType().equals(Channel.Type.GUILD_VOICE))
				.map(channel -> channel.getId()).collectList()).subscribe(value -> channelcodes = value);

		removeAFKChannel(event);
		
		while (true) {
			int randomNum = ThreadLocalRandom.current().nextInt(1, channelcodes.size() - 1);
			
			if (channelcodes.get(randomNum) != curr) {
				return channelcodes.get(randomNum);
			}
		}

	}
	private void removeAFKChannel(MessageCreateEvent event) {
		event.getGuild().flatMap(guild -> guild.getAfkChannel().map(channel -> channel.getId())).subscribe(value -> afkchan = value);
		channelcodes.remove(afkchan);
		channelcodes.remove(Snowflake.of("591086429745184769"));
		channelcodes.remove(Snowflake.of("591085589500264479"));
	}
	public void doloUsers(MessageCreateEvent event) {
		
		event.getMember().map(member -> member.getVoiceState().flatMap(vs -> vs.getChannel().map(channel -> channel.getId())).subscribe(value -> curruser = value));
		getAllUsers(event);
		event.getMessage().getAuthorAsMember().map(member -> member.getId()).subscribe(value -> caller = value);
		
		Snowflake moveto = randVoice(event, curruser);
		
		//Snowflake afkchan = event.getGuild().map(guild -> guild.getAfkChannel().map(afk -> afk.getId()).subscribe());
		for (VoiceState s: voicestates) {		
			if (s.getUserId().asLong() != caller.asLong()) {
				specChanneluser(event, s.getUserId(), s.getGuildId(), moveto);
			}
		}
		
	}
}
