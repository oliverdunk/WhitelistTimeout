package me.olivervscreeper.whitelisttimeout;

import me.olivervscreeper.networkutilities.command.Command;
import me.olivervscreeper.networkutilities.command.CommandManager;
import me.olivervscreeper.networkutilities.messages.Message;
import me.olivervscreeper.networkutilities.utils.PasteUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.List;

/**
 * Created by OliverVsCreeper on 15/02/2015.
 */
public class WhitelistTimeout extends JavaPlugin {

    public void onEnable(){
        this.getLogger().info("WhitelistTimeout Enabled!");
        new CommandManager(this).registerCommands(this);
        this.saveDefaultConfig();
    }

    @Command(label = "whitelisttimeout", permission = "whitelisttimeout.use")
    public void onCommand(final Player player, List<String> args){
        new Message(Message.INFO).addRecipient(player).send("Checking Whitelist...");
        final FileConfiguration config = this.getConfig();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this,
                new Runnable() {
                    @Override
                    public void run() {
                        String unwhitelisted = "Unwhitelisted: ";
                        for(OfflinePlayer oplayer : Bukkit.getWhitelistedPlayers()){
                            if(oplayer == null) continue;

                            Calendar now = Calendar.getInstance();
                            Calendar then = Calendar.getInstance();
                            then.setTimeInMillis(oplayer.getLastPlayed());

                            System.out.println(oplayer.getName() + ": " + daysBetween(then, now));

                            if(daysBetween(then, now) > config.getInt("maxDaysSinceLastPlayed") | !oplayer.hasPlayedBefore()){
                                oplayer.setWhitelisted(false);
                                new Message(Message.INFO).addRecipient(player).send(oplayer.getName() + " was unwhitelisted.");
                                unwhitelisted = unwhitelisted + player.getName() + ", ";
                            }
                        }
                        new Message(Message.INFO).addRecipient(player).send("Operation finished.");
                        new Message(Message.INFO).addRecipient(player).send("Users Unwhitelisted: " + PasteUtils.paste(unwhitelisted));
                    }
                }, 1);
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        //assert: startDate must be before endDate
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

}

