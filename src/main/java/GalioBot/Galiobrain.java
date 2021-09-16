package GalioBot;


import java.time.Duration;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import java.util.Map;
import java.util.Timer;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;



public class Galiobrain {
	private final Map<String, Command> commands = new HashMap<>();
	private GatewayDiscordClient client;
	private Channels channels;
	private String newname = null;
	private BotUtilities bot;
	private Logger log;
	public static void main (String args[]) {
			new Galiobrain();
	}
	public Galiobrain() {
		bot = new BotUtilities();
		log = new Logger();
		SetupCommands();

		ButtonHandler btnHandler = new ButtonHandler(log);
		client = DiscordClientBuilder.create("MjQwMjQ2ODM1OTAwMzE3Njk2.WA6RTQ.uxBNh6ewAGkiXeKsH20TqN9g4mE")
                .build()
                .login()
                .block();
		
        client.getEventDispatcher().on(ReadyEvent.class)
        .subscribe(event -> {
          User self = event.getSelf();
          System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
        });
        
        //check if event comes from private message sent to console
        //add check if they are in voicechannel
        client.getEventDispatcher().on(MessageCreateEvent.class)
        .subscribe(event -> {
        	event.getMessage().getChannel().ofType(TextChannel.class).doOnNext(test -> {
                final String content = event.getMessage().getContent();
                for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                    if (content.split(" ")[0].equalsIgnoreCase('!' + entry.getKey())) {
                        entry.getValue().execute(event);
                        if (!content.startsWith("!delete")) {
                        	bot.addMessage(event);
                        }              
                        break;
                    }
                }
        	}).subscribe();
        	//seperate linkchecker into own class
        	
        	if (!event.getMessage().getData().attachments().toString().equals("[]")) {
        		Download.downloadImage(event.getMessage().getData().attachments());
        	}
        	ContentDisplay.checkForDownload(event.getMessage().getContent(), event);
            log.logText(event);
        });
        
        client.getEventDispatcher().on(VoiceStateUpdateEvent.class)
        .subscribe(event -> {
        	log.logVoice(event);
        });
        
        client.getEventDispatcher().on(ButtonInteractEvent.class).subscribe(
        		event-> {
        			btnHandler.handleEvent(event);
        		});
        
        //setup scheduler to run at 3am then every day after that
        setupScheduler();
        
        bot.setClient(client);
		channels = new Channels(client);
        client.onDisconnect().block();
	}

	private void setupScheduler() {
		MyScheduler scheduler = new MyScheduler(client);
        Timer t=new Timer();

        Duration today = Duration.ofMillis(System.currentTimeMillis());
        
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 3);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        if (today.toHoursPart() < 4 || today.toHoursPart() > 6) {
        	date.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        Duration tomorrow = Duration.ofMillis(date.getTimeInMillis());
        Duration diff = tomorrow.minus(today);
        System.out.println(diff.toSeconds());
        
        t.scheduleAtFixedRate(scheduler, diff.getSeconds()*1000,1000*60*60*24);
	}
	public void SetupCommands() {
		String comms[] = new String[] { "brando", "dallen", "jamie", "jose", 
				"john", "kevin", "ummz", "michael", "soloz", "umair"};
		
		for (int i = 0; i < comms.length; i ++) {
			commands.put(comms[i], event -> {
				bot.sendMessage(event, RandomUtilities.getNameMeme(event));
			});
		}
	    commands.put("decide", event -> {
	    	bot.sendMessage(event, RandomUtilities.decide(bot.parseArgs(event), 10000));
	    });
	    commands.put("day", event -> {
	    	setChannel(event);
	    });
	    commands.put("roll", event -> {
	    	bot.sendMessage(event, "" + RandomUtilities.roll(RandomUtilities.parseForInt(event)));
	    });
	    commands.put("help", event -> {
	    	event.getMessage().getChannel().subscribe( chan -> {
	    		bot.sendCommandList(chan, commands);
	    	});
	    });
	    commands.put("delete",  event -> {
	    	bot.cleanMessages(event, "Cleaned Messages");
	    });
	    commands.put("yolo", event -> {
	    	//randomChannels(event);
	    	bot.sendMessage(event, "Yolo is currently disabled");
	    });

	    commands.put("dolo", event -> {
	    	bot.sendMessage(event, "dolo yourself bitch");
	    	//doloUser(event);
	    });
	    commands.put("tarkov", event -> {
	    	event.getMessage().getChannel().subscribe( chan -> {
	    		Tarkov.sendTarkovRandom(chan, bot.parseArgs(event));
	    	});
	    });
	    commands.put("gear", event -> {
	    	event.getMessage().getChannel().subscribe( chan -> {
	    		Tarkov.sendRandomTarkovGear(chan, bot.parseArgs(event));
	    	});
	    });
	    commands.put("join", event -> {
	    	bot.join(event);
	    });
	    commands.put("leave", event -> {
	    	bot.leave(event);
	    });
	    commands.put("votekick", event -> {
	    	bot.sendVoteButton(event);
	    });
	    commands.put("scoreboard", event -> {
	    	event.getMessage().getChannel().subscribe( chan -> {
	    		log.sendScoreBoard(chan);
	    	});
	    });
	    commands.put("gl", event -> {
	    	event.getMessage().getChannel().subscribe( chan -> {
	    		Tarkov.sendGrenadeLauncher(chan);
	    	});
	    });
	    commands.put("links", event -> {
	    	event.getMessage().getChannel().subscribe( chan -> {
	    		Tarkov.sendUsefulLinks(chan);
	    	});
	    });
	}
		
	public void setChannel(MessageCreateEvent event) {
		
		if (!channels.isReady()) {
			bot.sendMessage(event, channels.getTimeRemaining());
			return;
		}
		
		while (true) {
			newname = channels.getChannelNameDay();
			
			if (newname.equals(channels.getCurrentName(event))) {
				newname = channels.getChannelNameDay();
			} else {
				break;
			}
		}
		
		bot.sendMessageDelay(event, "Changing Channel Name to: " + newname);
		bot.changeChannelName(event, newname);
	}
}
