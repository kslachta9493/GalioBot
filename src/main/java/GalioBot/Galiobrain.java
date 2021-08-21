package GalioBot;


import java.time.Duration;
import java.util.Arrays;
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



public class Galiobrain {
	private final Map<String, Command> commands = new HashMap<>();
	private GatewayDiscordClient client;
	private Channels channels;
	private Movement move;
	private String newname = null;
	private BotUtilities bot;
	private MyScheduler scheduler;
	private Logger log;
	public static void main (String args[]) {
			new Galiobrain();
	}
	public Galiobrain() {
		bot = new BotUtilities();
		log = new Logger();
		SetupCommands();

		ButtonHandler btnHandler = new ButtonHandler(log);
		client = DiscordClientBuilder.create("")
                .build()
                .login()
                .block();
		
        client.getEventDispatcher().on(ReadyEvent.class)
        .subscribe(event -> {
          User self = event.getSelf();
          System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
        });
        
        //check if event comes from private message sent to console
        client.getEventDispatcher().on(MessageCreateEvent.class)
        .subscribe(event -> {
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
            //bot.log(event);
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
		move = new Movement();
        client.onDisconnect().block();
	}

	private void setupScheduler() {
        scheduler = new MyScheduler(client);
        Delete deleter = new Delete();
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
        t.scheduleAtFixedRate(deleter,  diff.getSeconds()*1000,1000*60*60*24);
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
	    	bot.sendMessage(event, decide(event));
	    });
	    commands.put("day", event -> {
	    	setChannel(event);
	    });
	    commands.put("roll", event -> {
	    	bot.sendMessage(event, "" + RandomUtilities.roll(RandomUtilities.parseForInt(event)));
	    });
	    commands.put("help", event -> {
	    	bot.sendMessage(event, decide(event));
	    });
	    commands.put("delete",  event -> {
	    	bot.cleanMessages(event, "Cleaned Messages");
	    });
	    commands.put("add", event -> {
	    	bot.sendMessageDelay(event, addCommand(event));
	    });
	    commands.put("yolo", event -> {
	    	//randomChannels(event);
	    	bot.sendMessage(event, "Yolo is currently disabled");
	    });
	    commands.put("return", event -> {
	    	returnChannelUser(event);
	    });
	    commands.put("dolo", event -> {
	    	bot.sendMessage(event, "dolo yourself bitch");
	    	//doloUser(event);
	    });
	    commands.put("test", event -> {
	    	bot.sendButtonMessage(event, "Test", "test", "Try this Button Below");
	    });
	    commands.put("test2", event -> {
	    	bot.sendButtonMessage(event, "Test2", "test2", "Try this Button Below");
	    });
	    commands.put("tarkov", event -> {
	    	bot.sendMessageDelay(event, RandomUtilities.getTarkovMap(10000));
	    });
	    /*
	    commands.put("tarkov", event -> {
	    	event.getMessage().getChannel().subscribe( chan -> {
	    		Tarkov.sendTarkovRandom(chan, parseArgs(event));
	    	});
	    });
	    */
	    commands.put("galio", event -> {
	    	bot.sendMessage(event, RandomUtilities.getTarkovMap(10000));
	    });
	    commands.put("yar", event -> {
	    	bot.sendJohnBitch(event, "Stop pushing my buttons");
	    });
	    commands.put("rr", event -> {
	    	bot.sendButtonTest(event);
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
	
	private String[] parseArgs(MessageCreateEvent event) {
		return event.getMessage().getContent().split(" ");
	}
	
	private void returnChannelUser(MessageCreateEvent event) {
		move.returnChannelUser(event);
		bot.sendMessage(event, "Returning users");
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

	public String addCommand(MessageCreateEvent events) {
		String[] args = events.getMessage().getContent().split(" ");
		String output = "Commands Added:";
		if (args.length == 1) {
			return "no u";
			
		} else {
			
			for (int i = 1; i < args.length; i++) {
				if (commands.containsKey(args[i])) {
					bot.sendBotMessage(events, (args[i]+ " already exists"), 5);
				} else {
					commands.put(args[i], event -> {
						bot.sendMessageDelay(event, RandomUtilities.getNameMeme(events));
					});
					output = output + " " + args[i];
				}
			}
		}

		return output;
	}

	public String help() {
		String output = ">>> **Commands Available\n**";
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
		return output;
	}

	private String decide(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if (args.length == 1) {
			return "no u";
		} else {
			return RandomUtilities.decide(args, 10000);
		}
	}
}
