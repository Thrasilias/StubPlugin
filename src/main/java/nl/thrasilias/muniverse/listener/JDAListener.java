package nl.thrasilias.muniverse.listener;

import com.vdurmont.emoji.EmojiParser;
import litebans.api.Entry;
import litebans.api.Events;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.thrasilias.muniverse.Logger;
import nl.thrasilias.muniverse.Main;
import nl.thrasilias.muniverse.bot.Bot;
import nl.thrasilias.muniverse.configuration.Config;
import nl.thrasilias.muniverse.configuration.Lang;
import nl.thrasilias.muniverse.util.ColorTranslator;
import nl.thrasilias.muniverse.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

public class JDAListener extends ListenerAdapter {
    private final Main plugin;

    public JDAListener(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReady(ReadyEvent event) {
        Logger.debug("Discord: Connected");

        TextChannel channel;
        if (Config.MODLOGS_CHANNEL != null && !Config.MODLOGS_CHANNEL.isEmpty()) {
            channel = event.getJDA().getTextChannelById(Config.MODLOGS_CHANNEL);
            if (channel != null) {
                plugin.getBot().setModlogsChannel(channel);
                registerEvents(plugin.getBot());
            } else {
                Logger.error("Could not register modlogs channel!");
            }
        }


        if (Config.CONSOLE_CHANNEL != null && !Config.CONSOLE_CHANNEL.isEmpty()) {
            channel = event.getJDA().getTextChannelById(Config.CONSOLE_CHANNEL);
            if (channel != null) {
                plugin.getBot().setConsoleChannel(channel);
            } else {
                Logger.error("Could not register console channel!");
            }
        }

        if (Config.CHAT_CHANNEL != null && !Config.CHAT_CHANNEL.isEmpty()) {
            channel = event.getJDA().getTextChannelById(Config.CHAT_CHANNEL);
            if (channel != null) {
                plugin.getBot().setChatChannel(channel);
                plugin.getBot().sendMessageToDiscord(Lang.SERVER_ONLINE);
            } else {
                Logger.error("Could not register chat channel!");
            }
        }
    }

    @Override
    public void onResume(ResumedEvent event) {
        Logger.debug("Discord: Resumed connection");
        setChannel(event.getJDA());
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        Logger.debug("Discord: Re-connected");
        setChannel(event.getJDA());
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        Logger.debug("Discord: Disconnected");
        setChannel(null);
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        Logger.debug("Discord: Shutting down");
        setChannel(null);
    }

    private void setChannel(JDA jda) {
        TextChannel channel = jda != null ? jda.getTextChannelById(Config.CHAT_CHANNEL) : null;
        plugin.getBot().setChatChannel(channel);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor() == event.getJDA().getSelfUser()) {
            return; // dont echo
        }
        if (event.isWebhookMessage()) {
            return; // do not listen to webhooks
        }
        if (event.getMessage().getChannel().getId().equals(Config.CHAT_CHANNEL)) {
            String content = event.getMessage().getContentRaw();
            if (content.startsWith("!") && content.length() > 1) {
                String[] split = content.split(" ");
                String command = split[0].substring(1).toLowerCase();
                String[] args = Arrays.copyOfRange(split, 1, split.length);
                plugin.getBot().handleCommand(event.getAuthor().getName(), command, args);
            } else {
                StringBuilder message = new StringBuilder(EmojiParser.parseToAliases(event.getMessage().getContentDisplay()));
                List<Message.Attachment> att = event.getMessage().getAttachments();
                for (Message.Attachment attachment : att.subList(0, att.size() > 3 ? 3 : att.size())) {
                    if (message.length() > 0) {
                        message.append(" ");
                    }
                    message.append(attachment.getUrl());
                }
                Map<String, Object> roles = Config.DISCORD_ROLES;
                Member user = event.getMember();

                String extraPrefix = "";
                int position = 0;
                for (Role urole : user.getRoles()){
                    int roleposition = urole.getPosition();
                    if (roleposition > position){
                        String ID = urole.getId();
                        if (roles.containsKey(ID)){
                            String val = roles.get(ID).toString();
                            String roleColor = (val.isEmpty()) ? "<#"+Integer.toHexString(urole.getColorRaw())+">" : val;
                            position = roleposition;
                            extraPrefix = " §7[" + roleColor + urole.getName().replaceAll("[^\\w\\s\\+\\-*\\/\\^\\&\\:\\•\\.\\\\|\\(\\)\\>\\<\\[\\]\\{\\}]","").trim() + "§7]";
                        }

                    }
                }
                plugin.getBot().sendMessageToMinecraft(Lang.MINECRAFT_CHAT_FORMAT
                        .replace("{displayname}", ColorTranslator.translateAlternateColorCodes(extraPrefix)+ " §f§l" + event.getAuthor().getName())
                        .replace("{message}", ColorTranslator.translateAlternateColorCodes(message.toString()))
                );
            }
        } else if (event.getMessage().getChannel().getId().equals(Config.CONSOLE_CHANNEL)) {
            if (event.getAuthor() == null || event.getAuthor().getId() == null || plugin.getBot().getClient().getSelfUser().getId() == null || event.getAuthor().getId().equals(plugin.getBot().getClient().getSelfUser().getId())) {
                return;
            }
            new BukkitRunnable() {
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw().replace("@here","@.here").replace("@everyone","@.everyone").replace("<@&","<@.&"));
                }
            }.runTask(plugin);
        }
    }


    public void registerEvents(Bot bot) {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                TextChannel minecraftLogsChannel = bot.getClient().getTextChannelById(Config.MODLOGS_CHANNEL);
                if (minecraftLogsChannel != null) {
                    String reason = entry.getReason();
                    String moderatorName = entry.getExecutorName();
                    Date time = new Date();
                    String formattedTime = (new SimpleDateFormat("HH:mm:ss")).format(time);
                    long untilvalue = entry.getDateEnd();
                    String formattedUntil;


                    if (entry.isPermanent()){
                        formattedUntil = "for **permanently**";
                    } else {
                        formattedUntil = "for **" + TimeUtil.formatDateDiff(untilvalue, entry.getDateStart()) + "**";
                    }

                    String verb; String color; String username;

                    try{
                        ConfigurationSection punishmentConfig = Config.MODLOGS.getConfigurationSection(entry.getType());
                        verb = punishmentConfig.getString("verb");
                        color = punishmentConfig.getString("color").toUpperCase().replace("#","");
                    } catch (Exception e){
                        verb = entry.getType();
                        color = "000000";
                    }

                    try{
                        username = Bukkit.getOfflinePlayer(UUID.fromString(entry.getUuid())).getName();
                    } catch (Exception e){
                        username = entry.getUuid();
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Integer.parseInt(color, 16));
                    embed.setAuthor(username, null, "https://www.mc-heads.net/avatar/" + username + ".png");
                    embed.setTitle("`" + username + "` has been __" + (entry.isIpban() ? "ip" : "") + verb + "__ by `" + moderatorName + "`:");
                    String description = "";
                    description += "**·** " + formattedUntil;
                    description += "\n>>> " + reason;
                    embed.setDescription(description);
                    embed.setFooter(formattedTime + " | " + entry.getUuid());
                    minecraftLogsChannel.sendMessage(embed.build()).queue();
                }
            }
        });
    }

}